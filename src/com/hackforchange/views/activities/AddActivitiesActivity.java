package com.hackforchange.views.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.reminders.RemindersDAO;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.reminders.Reminders;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Add a new activity to an existing project
 */
// TODO: Make sure required text fields are not empty
public class AddActivitiesActivity extends SherlockActivity {
    static final int DATE_DIALOG = 0, TIME_DIALOG = 1;
    protected int mYear, mMonth, mDay, mHour, mMinute, dayOfWeek;
    protected EditText title, startDate, endDate, notes, orgs, comms;
    protected EditText mondayTime, tuesdayTime, wednesdayTime, thursdayTime, fridayTime, saturdayTime, sundayTime;
    protected CheckBox mondayCheckbox, tuesdayCheckbox, wednesdayCheckbox, thursdayCheckbox, fridayCheckbox, saturdayCheckbox, sundayCheckbox;
    protected Button submitButton;
    protected boolean startOrEnd; // used in OnDateSetListener to distinguish between start date and end date field
    protected String initiatives;
    private int projectid;
    // used because we reuse the same listener for both fields
    protected Activities a;
    protected Reminders r;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addactivitiesactivity);

        // get the owner project
        projectid = getIntent().getExtras().getInt("projectid");
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // entering the reminder time
        mondayTime = (EditText) findViewById(R.id.mondayTime);
        mondayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
        // not doing this means the first click simply focuses the text field
        mondayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mondayCheckbox.isChecked()) {
                    dayOfWeek = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("timetodisplay", mondayTime.getText().toString());
                    showDialog(TIME_DIALOG, bundle);
                }
            }
        });

        tuesdayTime = (EditText) findViewById(R.id.tuesdayTime);
        tuesdayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
        // not doing this means the first click simply focuses the text field
        tuesdayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tuesdayCheckbox.isChecked()) {
                    dayOfWeek = 2;
                    Bundle bundle = new Bundle();
                    bundle.putString("timetodisplay", tuesdayTime.getText().toString());
                    showDialog(TIME_DIALOG, bundle);
                }
            }
        });

        wednesdayTime = (EditText) findViewById(R.id.wednesdayTime);
        wednesdayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
        // not doing this means the first click simply focuses the text field
        wednesdayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wednesdayCheckbox.isChecked()) {
                    dayOfWeek = 3;
                    Bundle bundle = new Bundle();
                    bundle.putString("timetodisplay", wednesdayTime.getText().toString());
                    showDialog(TIME_DIALOG, bundle);
                }
            }
        });

        thursdayTime = (EditText) findViewById(R.id.thursdayTime);
        thursdayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
        // not doing this means the first click simply focuses the text field
        thursdayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thursdayCheckbox.isChecked()) {
                    dayOfWeek = 4;
                    Bundle bundle = new Bundle();
                    bundle.putString("timetodisplay", thursdayTime.getText().toString());
                    showDialog(TIME_DIALOG, bundle);
                }
            }
        });

        fridayTime = (EditText) findViewById(R.id.fridayTime);
        fridayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
        // not doing this means the first click simply focuses the text field
        fridayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fridayCheckbox.isChecked()) {
                    dayOfWeek = 5;
                    Bundle bundle = new Bundle();
                    bundle.putString("timetodisplay", fridayTime.getText().toString());
                    showDialog(TIME_DIALOG, bundle);
                }
            }
        });

        saturdayTime = (EditText) findViewById(R.id.saturdayTime);
        saturdayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
        // not doing this means the first click simply focuses the text field
        saturdayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saturdayCheckbox.isChecked()) {
                    dayOfWeek = 6;
                    Bundle bundle = new Bundle();
                    bundle.putString("timetodisplay", saturdayTime.getText().toString());
                    showDialog(TIME_DIALOG, bundle);
                }
            }
        });

        sundayTime = (EditText) findViewById(R.id.sundayTime);
        sundayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
        // not doing this means the first click simply focuses the text field
        sundayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sundayCheckbox.isChecked()) {
                    dayOfWeek = 7;
                    Bundle bundle = new Bundle();
                    bundle.putString("timetodisplay", sundayTime.getText().toString());
                    showDialog(TIME_DIALOG, bundle);
                }
            }
        });

        // entering the start date
        startDate = (EditText) findViewById(R.id.startDate);
        startDate.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
        // not doing this means the first click simply focuses the text field
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrEnd = true;
                showDialog(DATE_DIALOG, null);
            }
        });

        // entering the end date
        endDate = (EditText) findViewById(R.id.endDate);
        endDate.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
        // not doing this means the first click simply focuses the text field
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrEnd = false;
                showDialog(DATE_DIALOG, null);
            }
        });

        title = (EditText) findViewById(R.id.title);
        notes = (EditText) findViewById(R.id.notes);
        orgs = (EditText) findViewById(R.id.orgs);
        comms = (EditText) findViewById(R.id.comms);

        mondayCheckbox = (CheckBox) findViewById(R.id.mondayCheckBox);
        mondayCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mondayCheckbox.isChecked())
                    mondayTime.setText("");
            }
        });

        tuesdayCheckbox = (CheckBox) findViewById(R.id.tuesdayCheckBox);
        tuesdayCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tuesdayCheckbox.isChecked())
                    tuesdayTime.setText("");
            }
        });

        wednesdayCheckbox = (CheckBox) findViewById(R.id.wednesdayCheckBox);
        wednesdayCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wednesdayCheckbox.isChecked())
                    wednesdayTime.setText("");
            }
        });

        thursdayCheckbox = (CheckBox) findViewById(R.id.thursdayCheckBox);
        thursdayCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!thursdayCheckbox.isChecked())
                    thursdayTime.setText("");
            }
        });

        fridayCheckbox = (CheckBox) findViewById(R.id.fridayCheckBox);
        fridayCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fridayCheckbox.isChecked())
                    fridayTime.setText("");
            }
        });

        saturdayCheckbox = (CheckBox) findViewById(R.id.saturdayCheckBox);
        saturdayCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!saturdayCheckbox.isChecked())
                    saturdayTime.setText("");
            }
        });

        sundayCheckbox = (CheckBox) findViewById(R.id.sundayCheckBox);
        sundayCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sundayCheckbox.isChecked())
                    sundayTime.setText("");
            }
        });

        submitButton = (Button) findViewById(R.id.submitbutton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a = new Activities();

                // save the start and end date
                DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date date = parser.parse(startDate.getText().toString());
                    a.setStartDate(date.getTime());
                    date = parser.parse(endDate.getText().toString());
                    a.setEndDate(date.getTime());
                } catch (ParseException e) {
                }

                // save title and other params
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

                // don't forget to save the associated project
                a.setProjectid(projectid);

                ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());

                // createdActivityId is the ID of the activity we just created
                // we save it along with the reminders in the reminder table so that
                // we know which activity the reminder is for
                int createdActivityId = aDao.addActivities(a);

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
                            r.setActivityid(createdActivityId);
                            r.setRemindTime(c.getTimeInMillis());
                            rDao.addReminders(r, getApplicationContext());
                        } catch (ParseException e) {
                        }
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
                            r.setActivityid(createdActivityId);
                            r.setRemindTime(c.getTimeInMillis());
                            rDao.addReminders(r, getApplicationContext());
                        } catch (ParseException e) {
                        }
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
                            r.setActivityid(createdActivityId);
                            r.setRemindTime(c.getTimeInMillis());
                            rDao.addReminders(r, getApplicationContext());
                        } catch (ParseException e) {
                        }
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
                            r.setActivityid(createdActivityId);
                            r.setRemindTime(c.getTimeInMillis());
                            rDao.addReminders(r, getApplicationContext());
                        } catch (ParseException e) {
                        }
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
                            r.setActivityid(createdActivityId);
                            r.setRemindTime(c.getTimeInMillis());
                            rDao.addReminders(r, getApplicationContext());
                        } catch (ParseException e) {
                        }
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
                            r.setActivityid(createdActivityId);
                            r.setRemindTime(c.getTimeInMillis());
                            rDao.addReminders(r, getApplicationContext());
                        } catch (ParseException e) {
                        }
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
                            r.setActivityid(createdActivityId);
                            r.setRemindTime(c.getTimeInMillis());
                            rDao.addReminders(r, getApplicationContext());
                        } catch (ParseException e) {
                        }
                    }
                }
                finish();
            }
        });
    }

    protected TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    String timeToDisplay = String.format("%02d:%02d %s", ((mHour / 12) > 0 ? ((mHour == 12) ? 12 : (mHour - 12)) : ((mHour == 0) ? 12 : mHour)), mMinute, ((mHour / 12) > 0 ? "PM" : "AM"));
                    switch (dayOfWeek) {
                        case 1:
                            mondayTime.setText(timeToDisplay); //sets the chosen date in the text view
                            break;
                        case 2:
                            tuesdayTime.setText(timeToDisplay); //sets the chosen date in the text view
                            break;
                        case 3:
                            wednesdayTime.setText(timeToDisplay); //sets the chosen date in the text view
                            break;
                        case 4:
                            thursdayTime.setText(timeToDisplay); //sets the chosen date in the text view
                            break;
                        case 5:
                            fridayTime.setText(timeToDisplay); //sets the chosen date in the text view
                            break;
                        case 6:
                            saturdayTime.setText(timeToDisplay); //sets the chosen date in the text view
                            break;
                        case 7:
                            sundayTime.setText(timeToDisplay); //sets the chosen date in the text view
                            break;
                    }
                    removeDialog(TIME_DIALOG); // remember to remove the dialog or onCreateDialog will NOT be called again! We need it to be called afresh
                    // each time the reminder time field is clicked because we prepopulate the date picker with different
                    // times in EditProjectActivity.java's overriden onCreateDialog
                    // http://stackoverflow.com/questions/2222648/change-the-contents-of-an-android-dialog-box-after-creation
                }
            };

    // the callback received when the user "sets" the date in the dialog
    protected DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    if (startOrEnd)
                        startDate.setText(String.format("%02d/%02d/%4d", (mMonth + 1), mDay, mYear)); //sets the chosen date in the text view
                    else
                        endDate.setText(String.format("%02d/%02d/%4d", (mMonth + 1), mDay, mYear)); //sets the chosen date in the text view
                    removeDialog(DATE_DIALOG); // remember to remove the dialog or onCreateDialog will NOT be called again! We need it to be called afresh
                    // each time either startDate or endDate is clicked because we prepopulate the date picker with different
                    // dates for startDate and endDate in EditProjectActivity.java's overriden onCreateDialog
                    // http://stackoverflow.com/questions/2222648/change-the-contents-of-an-android-dialog-box-after-creation
                }
            };

    @Override
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        switch (id) {
            case DATE_DIALOG:
                // get the current date
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
            case TIME_DIALOG:
                // get the prepopulated date
                DateFormat parser = new SimpleDateFormat("hh:mm aaa");
                Date date;
                c = Calendar.getInstance();
                String timeToDisplay = bundle.getString("timetodisplay");
                try {
                    date = parser.parse(timeToDisplay);
                    c.setTime(date);
                } catch (ParseException e) {
                } finally {
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);
                    return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);
                }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // provide a back button on the actionbar
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}