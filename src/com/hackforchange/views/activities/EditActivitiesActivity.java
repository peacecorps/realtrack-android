package com.hackforchange.views.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.reminders.RemindersDAO;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.reminders.Reminders;
import com.hackforchange.reminders.NotificationService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
 * Presents an activity that lets you edit an EXISTING activities
 * Reuses most of the code as well as the layout of AddActivitiesActivity
 * Pressing the back key will exit the activity WITHOUT modding the activities
 */
public class EditActivitiesActivity extends AddActivitiesActivity {
    private int id;
    private int projectid; // used to update the existing activity
    private ArrayList<Reminders> reminders_data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // read in the ID of the activities that this activity must display details of
        id = getIntent().getExtras().getInt("activitiesid");
    }

    @Override
    public void onResume() {
        super.onResume();

        // pre-populate the fields with the activities details from the DB
        // the user can then change them if he so desires (the changes are handled
        // from AddActivitiesActivity
        ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
        a = aDao.getActivityWithId(id);

        // populate the title, dates and other text fields
        title.setText(a.getTitle());
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Date d = new Date(a.getStartDate());
        startDate.setText(parser.format(d));
        d = new Date(a.getEndDate());
        endDate.setText(parser.format(d));
        notes.setText(a.getNotes());
        orgs.setText(a.getOrgs());
        comms.setText(a.getComms());
        projectid = a.getProjectid();

        // populate the initiatives checkboxes
        String[] initiativesList = a.getInitiatives().split("\\|");
        for (int i = 0; i < initiativesList.length; i++) {
            if (initiativesList[i].equals("1")) {
                switch (i) {
                    case 0:
                        ((CheckBox) findViewById(R.id.widCheckBox)).setChecked(true);
                        break;
                    case 1:
                        ((CheckBox) findViewById(R.id.youthCheckBox)).setChecked(true);
                        break;
                    case 2:
                        ((CheckBox) findViewById(R.id.malariaCheckBox)).setChecked(true);
                        break;
                    case 3:
                        ((CheckBox) findViewById(R.id.ECPACheckBox)).setChecked(true);
                        break;
                    case 4:
                        ((CheckBox) findViewById(R.id.foodSecurityCheckBox)).setChecked(true);
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

        // change the submit button listener to UPDATE the existing activities instead of creating a NEW one
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a = new Activities();
                DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date date = parser.parse(startDate.getText().toString());
                    a.setStartDate(date.getTime());
                    date = parser.parse(endDate.getText().toString());
                    a.setEndDate(date.getTime());
                } catch (ParseException e) {
                }
                a.setTitle(title.getText().toString());
                a.setNotes(notes.getText().toString());
                a.setOrgs(orgs.getText().toString());
                a.setComms(comms.getText().toString());

                // store initiatives in compact form "x|x|x|x|x" where the first x is WID, second is Youth etc
                // this order MUST match the DisplayActivitiesActivity.AllInits array
                // If x == 1, this activity has the corresponding initiative, if 0 then it doesn't.
                initiatives = (((CheckBox) findViewById(R.id.widCheckBox)).isChecked() ? "1" : "0") + "|" +
                        (((CheckBox) findViewById(R.id.youthCheckBox)).isChecked() ? "1" : "0") + "|" +
                        (((CheckBox) findViewById(R.id.malariaCheckBox)).isChecked() ? "1" : "0") + "|" +
                        (((CheckBox) findViewById(R.id.ECPACheckBox)).isChecked() ? "1" : "0") + "|" +
                        (((CheckBox) findViewById(R.id.foodSecurityCheckBox)).isChecked() ? "1" : "0");
                a.setInitiatives(initiatives);

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
                            Log.e("burra", date.getDay() + "");
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
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                // get the prepopulated date
                DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
                Date date;
                try {
                    if (startOrEnd)
                        date = parser.parse(startDate.getText().toString());
                    else
                        date = parser.parse(endDate.getText().toString());

                    final Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                    return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
                } catch (ParseException e) {
                }
                break;
        }
        return null;
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
}