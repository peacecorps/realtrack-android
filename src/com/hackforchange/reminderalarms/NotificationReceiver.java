package com.hackforchange.reminderalarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.backend.reminders.RemindersDAO;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.models.reminders.Reminders;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class receives the boot completed event whenever the phone is started up
 * Its job is to schedule an alarm for this app's notifications
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static ArrayList<Reminders> reminders_data;
    private static ArrayList<Participation> participation_data;

    public void onReceive(Context context, Intent intent) {
        // in our case intent will always be BOOT_COMPLETED, so we can just set
        // the alarm
        // Note that a BroadcastReceiver is *NOT* a Context. Thus, we can't use
        // "this" whenever we need to pass a reference to the current context.
        // Thankfully, Android will supply a valid Context as the first parameter

        // schedule an alarm
        // we separate this code because it's also called from WelcomeActivity
        scheduleAlarm(context);
    }

    public static void scheduleAlarm(Context context) {
        handleReminders(context);
    }

    private static void handleReminders(Context context) {
        // read in ALL reminders from the reminder table and set alarms for each one
        RemindersDAO rDao = new RemindersDAO(context);
        reminders_data = rDao.getAllReminders();
        ParticipationDAO pDao = new ParticipationDAO(context);
        for (Reminders r : reminders_data) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(r.getRemindTime());

            // cancel any participation events scheduled in the future (from now) from the Participations table and also the associated
            // alarms for those events
            participation_data = pDao.getAllParticipationsForReminderId(r.getId());
            participation_data = pDao.getAllUnservicedParticipationsForReminderId(r.getId());
            for (Participation p : participation_data) {
                Calendar unservicedCalendar = Calendar.getInstance();
                unservicedCalendar.setTimeInMillis(p.getDate());

                if (Calendar.getInstance().compareTo(unservicedCalendar) == -1) {
                    pDao.deleteParticipation(p.getId());
                    Intent notifIntent = new Intent(context, NotificationService.class);
                    notifIntent.putExtra("participationid", p.getId());
                    notifIntent.setAction(Intent.ACTION_VIEW); // unpredictable android crap again. without an action, the extras will NOT be sent!!
                    PendingIntent pendingIntent = PendingIntent.getService(context, p.getId(), notifIntent, PendingIntent.FLAG_UPDATE_CURRENT); // remember to distinguish between pendingintents using Participation.id as the request code
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                    Log.e("burra", "deleting " + p.getId() + " and its associated alarms");
                }
            }

            ActivitiesDAO aDao = new ActivitiesDAO(context);
            Activities a = aDao.getActivityWithId(r.getActivityid());
            // if the activity has ended, delete the reminder and do not create any alarms
            // note that if the activity's end date was 9/9/2013, reminders up to (but not including) 9/10/2013 00:00 will
            // show up
            // we do NOT kill pending unserviced participations so that the user can still fill them in from the pending
            // screen; he will just never get to see them via a notification
            Calendar activityCalendar = Calendar.getInstance();
            activityCalendar.setTimeInMillis(a.getEndDate());
            activityCalendar.add(Calendar.DAY_OF_WEEK, 1); //roll over to the start of the next day
            if (c.compareTo(activityCalendar) == 1) {
                rDao.deleteReminders(r.getId(), context);
                Log.e("burra", "this activity is over. deleting reminder " + r.getId());
                return;
            }

            // if we're already past the time we want in this week, schedule it for the same time next week
            if (r.getRemindTime() < System.currentTimeMillis()) {
                // set the reminder for the same day and time next week
                c.add(Calendar.DAY_OF_WEEK, 7);
                // update the reminder time in the Reminders table, which in turn, schedules this new participation event
                // by calling this same NotificationReceiver function
                r.setRemindTime(c.getTimeInMillis());
                // if the activity ends within the next 7 days, delete the reminder since it would have been shown after 7 days
                // and do not create any alarms
                // note that if the activity's end date was 9/9/2013, reminders up to (but not including) 9/10/2013 00:00 will
                // show up
                // we do NOT kill pending unserviced participations so that the user can still fill them in from the pending
                // screen; he will just never get to see them via a notification
                if (c.compareTo(activityCalendar) == 1) {
                    rDao.deleteReminders(r.getId(), context);
                    Log.e("burra", "this activity will be over within the week. deleting reminder " + r.getId());
                    return;
                }
                rDao.updateReminders(r, context);
                Log.e("burra", "already passed this week, scheduling for next week " + c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH));
                return;
            }

            Intent notifIntent = new Intent(context, NotificationService.class);
            notifIntent.putExtra("reminderid", r.getId());
            notifIntent.setAction(Intent.ACTION_VIEW); // unpredictable android crap again. without an action, the extras will NOT be sent!!
            PendingIntent pendingIntent = PendingIntent.getService(context, r.getId(), notifIntent, PendingIntent.FLAG_UPDATE_CURRENT); // remember to distinguish between pendingintents using Reminders.id as the request code
            Log.e("burra", "scheduling for " + c.get(Calendar.MONTH) + "/" + c.get(Calendar.DAY_OF_MONTH) + ", " + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE));

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);

            c.clear();
        }
    }
}