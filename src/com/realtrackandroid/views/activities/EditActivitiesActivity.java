package com.realtrackandroid.views.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ActivitiesDAO;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.reminderalarms.NotificationService;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.GlossaryDialog;
import com.realtrackandroid.views.help.HelpDialog;

/*
 * Presents an activity that lets you edit an EXISTING activities
 * Reuses most of the code as well as the layout of AddActivitiesActivity
 * Pressing the back key will exit the activity WITHOUT modding the activities
 */
public class EditActivitiesActivity extends AddActivitiesActivity {
  private int id;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    id = getIntent().getExtras().getInt("activitiesid");
    ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
    a = aDao.getActivityWithId(id);
    projectid = a.getProjectid();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // provide a back button on the actionbar
        finish();
        break;
      case R.id.action_help:
        HelpDialog helpDialog = new HelpDialog();
        helpDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        helpDialog.show(getSupportFragmentManager(), "helpdialog");
        break;
      case R.id.action_framework:
        FrameworkInfoDialog frameworkInfoDialog = new FrameworkInfoDialog();
        frameworkInfoDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        frameworkInfoDialog.show(getSupportFragmentManager(), "frameworkinfodialog");
        break;
      case R.id.action_glossary:
        GlossaryDialog glossaryDialog = new GlossaryDialog();
        glossaryDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        glossaryDialog.show(getSupportFragmentManager(), "glossarydialog");
        break;
      case R.id.action_save:
        updateActivity();
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  private void updateActivity() {

    a = new Activities();

    a.setProjectid(projectid);
    a.setId(id);

    if (!requiredFragment.setFields(a))
      return;

    optionalFragment.setFields(a);

    ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
    aDao.updateActivities(a);

    remindersFragment.setFields(a, id);

    finish();
  }

  // remove all the alarms associated with a reminder
  public static void deleteAlarmsForReminder(Context context, int reminderid) {
    Intent notifIntent = new Intent(context, NotificationService.class);
    notifIntent.putExtra("reminderid", reminderid); // not necessary for alarmManager.cancel to
                                                    // match pending intents but leaving it here
                                                    // anyway
    notifIntent.setAction(Intent.ACTION_VIEW); // unpredictable android crap again. without an
                                               // action, the extras will NOT be sent!!
    PendingIntent pendingIntent = PendingIntent.getService(context, reminderid, notifIntent,
            PendingIntent.FLAG_UPDATE_CURRENT); // remember to distinguish between pendingintents
                                                // using Reminders.id as the request code
    pendingIntent.cancel();
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.cancel(pendingIntent);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }
}