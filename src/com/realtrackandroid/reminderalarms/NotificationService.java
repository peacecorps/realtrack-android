package com.realtrackandroid.reminderalarms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ActivitiesDAO;
import com.realtrackandroid.backend.activities.ParticipationDAO;
import com.realtrackandroid.backend.reminders.RemindersDAO;
import com.realtrackandroid.models.activities.Participation;
import com.realtrackandroid.views.participationsactive.RecordOrEditParticipationActivity;

/*
 * Source: http://it-ride.blogspot.com/2010/10/android-implementing-notification.html
 */
public class NotificationService extends Service {
  private PowerManager.WakeLock mWakeLock;

  private int reminderid;

  private static ArrayList<Participation> participation_data;

  private Participation p;

  /**
   * This is deprecated, but you have to implement it if you're planning on supporting devices with
   * an API level lower than 5 (Android 2.0).
   */
  @Override
  public void onStart(Intent intent, int startId) {
    handleIntent(intent);
  }

  /**
   * This is called on 2.0+ (API level 5 or higher). Returning START_NOT_STICKY tells the system to
   * not restart the service if it is killed because of poor resource (memory/cpu) conditions.
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    handleIntent(intent);
    return START_NOT_STICKY;
  }

  /**
   * In onDestroy() we release our wake lock. This ensures that whenever the Service stops (killed
   * for resources, stopSelf() called, etc.), the wake lock will be released.
   */
  public void onDestroy() {
    super.onDestroy();
    mWakeLock.release();
  }

  /**
   * Simply return null, since our Service will not be communicating with any other components. It
   * just does its work silently.
   */
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  /**
   * This is where we initialize. We call this when onStart/onStartCommand is called by the system.
   * We won't do anything with the intent here, and you probably won't, either.
   */
  private void handleIntent(Intent intent) {
    // obtain the wake lock
    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
    mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
    mWakeLock.acquire();

    reminderid = intent.getExtras().getInt("reminderid");

    // check the global background data setting
    ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    if (!cm.getBackgroundDataSetting()) {
      stopSelf();
      return;
    }

    // do the actual work, in a separate thread
    new PollTask().execute();
  }

  private class PollTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
      ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());

      // create a new participation event for the current time
      p = new Participation();
      p.setReminderid(reminderid);
      p.setActivityid((new RemindersDAO(getApplicationContext()).getReminderWithId(reminderid))
              .getActivityid());
      p.setMen09(0);
      p.setWomen09(0);
      p.setDate(Calendar.getInstance().getTimeInMillis());
      p.setServiced(false); // every new participation is un-serviced, by default
      pDao.addParticipation(p);

      NotificationReceiver.scheduleAlarm(getApplicationContext());

      // show all unserviced participations for this reminder
      participation_data = pDao.getAllUnservicedParticipationsForReminderId(reminderid);
      for (Participation p : participation_data) {
        int participationid = p.getId();

        Calendar c = Calendar.getInstance();
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy, EEEE, hh:mm aaa"); // example:
                                                                                 // 07/04/2013,
                                                                                 // Thursday, 6:13
                                                                                 // PM
        c.setTimeInMillis(p.getDate());

        // build a string to show the reminder date and time on the notification
        // participation -> activity -> activity's title
        String remindersText = new ActivitiesDAO(getApplicationContext()).getActivityWithId(
                p.getActivityid()).getTitle()
                + ", ";
        String dateTime = parser.format(p.getDate());
        remindersText += dateTime;

        // clicking on the notification must take the user to the record participation activity
        Intent notifIntent = new Intent(getApplicationContext(),
                RecordOrEditParticipationActivity.class);
        notifIntent.putExtra("participationid", participationid);
        notifIntent.putExtra("datetime", p.getDate());
        PendingIntent pendingIntent = TaskStackBuilder
                .create(getApplicationContext())
                // add all of RecordOrEditParticipationActivity's parents to the stack,
                // followed by RecordOrEditParticipationActivity itself
                // this works in conjunction with parentActivityName in AndroidManifest.xml
                .addNextIntentWithParentStack(notifIntent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        // PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
        // participationid, notifIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        // build notification
        Notification notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.icon).setContentTitle("Record Attendance")
                .setContentText(remindersText).setContentIntent(pendingIntent).getNotification();
        // Hide the notification after it is clicked
        notificationBuilder.flags |= Notification.FLAG_AUTO_CANCEL;

        // display notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(participationid, notificationBuilder);
      }
      return null;
    }

    /**
     * In here you should interpret whatever you fetched in doInBackground and push any
     * notifications you need to the status bar, using the NotificationManager. I will not cover
     * this here, go check the docs on NotificationManager.
     * <p/>
     * What you HAVE to do is call stopSelf() after you've pushed your notification(s). This will:
     * 1) Kill the service so it doesn't waste precious resources 2) Call onDestroy() which will
     * release the wake lock, so the device can go to sleep again and save precious battery.
     */
    @Override
    protected void onPostExecute(Void result) {
      // handle your data
      stopSelf();
    }
  }
}