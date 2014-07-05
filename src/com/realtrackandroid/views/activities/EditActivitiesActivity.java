package com.realtrackandroid.views.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.CheckBox;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ActivitiesDAO;
import com.realtrackandroid.backend.reminders.RemindersDAO;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.reminders.Reminders;
import com.realtrackandroid.reminderalarms.NotificationService;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.HelpDialog;

/*
 * Presents an activity that lets you edit an EXISTING activities
 * Reuses most of the code as well as the layout of AddActivitiesActivity
 * Pressing the back key will exit the activity WITHOUT modding the activities
 */
public class EditActivitiesActivity extends AddActivitiesActivity {
  private int id;
  private ArrayList<Reminders> reminders_data;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // read in the ID of the activities that this activity must display details of
    id = getIntent().getExtras().getInt("activitiesid");
  }

  @Override
  public void onResume() {
    // pre-populate the fields with the activities details from the DB
    // the user can then change them if he so desires (the changes are handled
    // from AddActivitiesActivity
    ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
    a = aDao.getActivityWithId(id);
    projectid = a.getProjectid();

    super.onResume();

    // populate the title, dates and other text fields
    super.title.setText(a.getTitle());
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    Date d = new Date(a.getStartDate());
    super.startDate.setText(parser.format(d));
    d = new Date(a.getEndDate());
    super.endDate.setText(parser.format(d));
    super.notes.setText(a.getNotes());
    super.cohort.setText(a.getCohort());
    super.orgs.setText(a.getOrgs());
    super.comms.setText(a.getComms());

    // populate the initiatives checkboxes
    String[] initiativesList = a.getInitiatives().split("\\|");
    for (int i = 0; i < initiativesList.length; i++) {
      if (initiativesList[i].equals("1")) {
        switch (i) {
          case 0:
            ((CheckBox) optionalFragment.getView().findViewById(R.id.malariaCheckBox)).setChecked(true);
            break;
          case 1:
            ((CheckBox) optionalFragment.getView().findViewById(R.id.ECPACheckBox)).setChecked(true);
            break;
          case 2:
            ((CheckBox) optionalFragment.getView().findViewById(R.id.foodSecurityCheckBox)).setChecked(true);
            break;
        }
      }
    }

    // populate the cspp checkboxes
    String[] csppList = a.getCspp().split("\\|");
    for (int i = 0; i < csppList.length; i++) {
      if (csppList[i].equals("1")) {
        switch (i) {
          case 0:
            ((CheckBox) optionalFragment.getView().findViewById(R.id.gendereqCheckBox)).setChecked(true);
            break;
          case 1:
            ((CheckBox) optionalFragment.getView().findViewById(R.id.hivaidsCheckBox)).setChecked(true);
            break;
          case 2:
            ((CheckBox) optionalFragment.getView().findViewById(R.id.technologyfordevelopmentCheckBox)).setChecked(true);
            break;
          case 3:
            ((CheckBox) optionalFragment.getView().findViewById(R.id.youthasresourcesCheckBox)).setChecked(true);
            break;
          case 4:
            ((CheckBox) optionalFragment.getView().findViewById(R.id.volunteerismCheckBox)).setChecked(true);
            break;
          case 5:
            ((CheckBox) optionalFragment.getView().findViewById(R.id.peoplewithdisabilitiesCheckBox)).setChecked(true);
            break;
        }
      }
    }

    // populate the reminder checkboxes
    RemindersDAO rDao = new RemindersDAO(getApplicationContext());
    reminders_data = rDao.getAllRemindersForActivityId(id);
    for (Reminders r : reminders_data) {
      parser = new SimpleDateFormat("hh:mm aaa");
      d = new Date(r.getRemindTime());
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(r.getRemindTime());
      switch (c.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.MONDAY:
          mondayCheckbox.setChecked(true);
          mondayTime.setText(parser.format(d));
          mondayTime.setTag(r.getId()); // will be used to update the reminder
          break;
        case Calendar.TUESDAY:
          tuesdayCheckbox.setChecked(true);
          tuesdayTime.setText(parser.format(d));
          tuesdayTime.setTag(r.getId()); // will be used to update the reminder
          break;
        case Calendar.WEDNESDAY:
          wednesdayCheckbox.setChecked(true);
          wednesdayTime.setText(parser.format(d));
          wednesdayTime.setTag(r.getId()); // will be used to update the reminder
          break;
        case Calendar.THURSDAY:
          thursdayCheckbox.setChecked(true);
          thursdayTime.setText(parser.format(d));
          thursdayTime.setTag(r.getId()); // will be used to update the reminder
          break;
        case Calendar.FRIDAY:
          fridayCheckbox.setChecked(true);
          fridayTime.setText(parser.format(d));
          fridayTime.setTag(r.getId()); // will be used to update the reminder
          break;
        case Calendar.SATURDAY:
          saturdayCheckbox.setChecked(true);
          saturdayTime.setText(parser.format(d));
          saturdayTime.setTag(r.getId()); // will be used to update the reminder
          break;
        case Calendar.SUNDAY:
          sundayCheckbox.setChecked(true);
          sundayTime.setText(parser.format(d));
          sundayTime.setTag(r.getId()); // will be used to update the reminder
          break;
      }
    }
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
        HelpDialog glossaryDialog = new HelpDialog();
        glossaryDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        glossaryDialog.setDisplayUrl("file:///android_asset/glossary.html");
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
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    try {
      Date date = parser.parse(startDate.getText().toString());
      a.setStartDate(date.getTime());
      date = parser.parse(endDate.getText().toString());
      date.setHours(23);
      date.setMinutes(59);
      a.setEndDate(date.getTime());
    } catch (ParseException e) {
      Toast.makeText(getApplicationContext(), R.string.fillrequiredfieldserrormessage, Toast.LENGTH_SHORT).show();
      return;
    }

    a.setTitle(title.getText().toString());
    if (a.getTitle().equals("")) {
      Toast.makeText(getApplicationContext(), R.string.fillrequiredfieldserrormessage, Toast.LENGTH_SHORT).show();
      return;
    }

    a.setNotes(notes.getText().toString());
    a.setCohort(cohort.getText().toString());
    a.setOrgs(orgs.getText().toString());
    a.setComms(comms.getText().toString());

    // store initiatives in compact form "x|x|x|x|x" where the first x is WID, second is Youth etc
    // this order MUST match the DisplayActivitiesActivity.AllInits array
    // If x == 1, this activity has the corresponding initiative, if 0 then it doesn't.
    initiatives = (((CheckBox) optionalFragment.getView().findViewById(R.id.malariaCheckBox)).isChecked() ? "1" : "0") + "|" +
            (((CheckBox) optionalFragment.getView().findViewById(R.id.ECPACheckBox)).isChecked() ? "1" : "0") + "|" +
            (((CheckBox) optionalFragment.getView().findViewById(R.id.foodSecurityCheckBox)).isChecked() ? "1" : "0");
    a.setInitiatives(initiatives);

    // store cspp in compact form "x|x|x"
    // If x == 1, this activity has the corresponding cspp, if 0 then it doesn't.
    cspp = (((CheckBox) optionalFragment.getView().findViewById(R.id.gendereqCheckBox)).isChecked() ? "1" : "0") + "|" +
            (((CheckBox) optionalFragment.getView().findViewById(R.id.hivaidsCheckBox)).isChecked() ? "1" : "0") + "|" +
            (((CheckBox) optionalFragment.getView().findViewById(R.id.technologyfordevelopmentCheckBox)).isChecked() ? "1" : "0") + "|" +
            (((CheckBox) optionalFragment.getView().findViewById(R.id.youthasresourcesCheckBox)).isChecked() ? "1" : "0") + "|" +
            (((CheckBox) optionalFragment.getView().findViewById(R.id.volunteerismCheckBox)).isChecked() ? "1" : "0") + "|" +
            (((CheckBox) optionalFragment.getView().findViewById(R.id.peoplewithdisabilitiesCheckBox)).isChecked() ? "1" : "0");
    a.setCspp(cspp);

    a.setProjectid(projectid);
    a.setId(id);

    ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
    aDao.updateActivities(a);

    // save reminders for this activity to the reminders table
    RemindersDAO rDao = new RemindersDAO(getApplicationContext());
    parser = new SimpleDateFormat("hh:mm aaa");

    if (mondayCheckbox.isChecked()) {
      if (mondayTime.getText() != null) {
        try {
          Date date = parser.parse(mondayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
          r = new Reminders();
          r.setActivityid(id);
          r.setRemindTime(c.getTimeInMillis());
          if (mondayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) mondayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getApplicationContext());
          } else { // add a new reminder
            rDao.addReminders(r, getApplicationContext());
          }
        } catch (ParseException e) {
        }
      }
    } else { // box was unchecked, remove any associated reminder for this day
      if (mondayTime.getTag() != null) {
        int reminderid = (Integer) mondayTime.getTag();
        rDao.deleteReminders(reminderid, getApplicationContext());
        EditActivitiesActivity.deleteAlarmsForReminder(getApplicationContext(), reminderid);
      }
    }

    if (tuesdayCheckbox.isChecked()) {
      if (tuesdayTime.getText() != null) {
        try {
          Date date = parser.parse(tuesdayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
          r = new Reminders();
          r.setActivityid(id);
          r.setRemindTime(c.getTimeInMillis());
          if (tuesdayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) tuesdayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getApplicationContext());
          } else { // add a new reminder
            rDao.addReminders(r, getApplicationContext());
          }
        } catch (ParseException e) {
        }
      }
    } else { // box was unchecked, remove any associated reminder for this day
      if (tuesdayTime.getTag() != null) {
        int reminderid = (Integer) tuesdayTime.getTag();
        rDao.deleteReminders(reminderid, getApplicationContext());
        EditActivitiesActivity.deleteAlarmsForReminder(getApplicationContext(), reminderid);
      }
    }

    if (wednesdayCheckbox.isChecked()) {
      if (wednesdayTime.getText() != null) {
        try {
          Date date = parser.parse(wednesdayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
          r = new Reminders();
          r.setActivityid(id);
          r.setRemindTime(c.getTimeInMillis());
          if (wednesdayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) wednesdayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getApplicationContext());
          } else { // add a new reminder
            rDao.addReminders(r, getApplicationContext());
          }
        } catch (ParseException e) {
        }
      }
    } else { // box was unchecked, remove any associated reminder for this day
      if (wednesdayTime.getTag() != null) {
        int reminderid = (Integer) wednesdayTime.getTag();
        rDao.deleteReminders(reminderid, getApplicationContext());
        EditActivitiesActivity.deleteAlarmsForReminder(getApplicationContext(), reminderid);
      }
    }


    if (thursdayCheckbox.isChecked()) {
      if (thursdayTime.getText() != null) {
        try {
          Date date = parser.parse(thursdayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
          r = new Reminders();
          r.setActivityid(id);
          r.setRemindTime(c.getTimeInMillis());
          if (thursdayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) thursdayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getApplicationContext());
          } else { // add a new reminder
            rDao.addReminders(r, getApplicationContext());
          }
        } catch (ParseException e) {
        }
      }
    } else { // box was unchecked, remove any associated reminder for this day
      if (thursdayTime.getTag() != null) {
        int reminderid = (Integer) thursdayTime.getTag();
        rDao.deleteReminders(reminderid, getApplicationContext());
        EditActivitiesActivity.deleteAlarmsForReminder(getApplicationContext(), reminderid);
      }
    }

    if (fridayCheckbox.isChecked()) {
      if (fridayTime.getText() != null) {
        try {
          Date date = parser.parse(fridayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
          r = new Reminders();
          r.setActivityid(id);
          r.setRemindTime(c.getTimeInMillis());
          if (fridayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) fridayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getApplicationContext());
          } else { // add a new reminder
            rDao.addReminders(r, getApplicationContext());
          }
        } catch (ParseException e) {
        }
      }
    } else { // box was unchecked, remove any associated reminder for this day
      if (fridayTime.getTag() != null) {
        int reminderid = (Integer) fridayTime.getTag();
        rDao.deleteReminders(reminderid, getApplicationContext());
        EditActivitiesActivity.deleteAlarmsForReminder(getApplicationContext(), reminderid);
      }
    }

    if (saturdayCheckbox.isChecked()) {
      if (saturdayTime.getText() != null) {
        try {
          Date date = parser.parse(saturdayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
          r = new Reminders();
          r.setActivityid(id);
          r.setRemindTime(c.getTimeInMillis());
          if (saturdayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) saturdayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getApplicationContext());
          } else { // add a new reminder
            rDao.addReminders(r, getApplicationContext());
          }
        } catch (ParseException e) {
        }
      }
    } else { // box was unchecked, remove any associated reminder for this day
      if (saturdayTime.getTag() != null) {
        int reminderid = (Integer) saturdayTime.getTag();
        rDao.deleteReminders(reminderid, getApplicationContext());
        EditActivitiesActivity.deleteAlarmsForReminder(getApplicationContext(), reminderid);
      }
    }

    if (sundayCheckbox.isChecked()) {
      if (sundayTime.getText() != null) {
        try {
          Date date = parser.parse(sundayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
          r = new Reminders();
          r.setActivityid(id);
          r.setRemindTime(c.getTimeInMillis());
          if (sundayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) sundayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getApplicationContext());
          } else { // add a new reminder
            rDao.addReminders(r, getApplicationContext());
          }
        } catch (ParseException e) {
        }
      }
    } else { // box was unchecked, remove any associated reminder for this day
      if (sundayTime.getTag() != null) {
        int reminderid = (Integer) sundayTime.getTag();
        rDao.deleteReminders(reminderid, getApplicationContext());
        EditActivitiesActivity.deleteAlarmsForReminder(getApplicationContext(), reminderid);
      }
    }

    finish();
  }

  // remove all the alarms associated with a reminder
  // also used by AllActivitiesActivity and DisplayActivitiesActivity
  public static void deleteAlarmsForReminder(Context context, int reminderid) {
    Intent notifIntent = new Intent(context, NotificationService.class);
    notifIntent.putExtra("reminderid", reminderid); // not necessary for alarmManager.cancel to match pending intents but leaving it here anyway
    notifIntent.setAction(Intent.ACTION_VIEW); // unpredictable android crap again. without an action, the extras will NOT be sent!!
    PendingIntent pendingIntent = PendingIntent.getService(context, reminderid, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT); // remember to distinguish between pendingintents using Reminders.id as the request code
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