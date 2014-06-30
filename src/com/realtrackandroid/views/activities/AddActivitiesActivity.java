package com.realtrackandroid.views.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ActivitiesDAO;
import com.realtrackandroid.backend.projects.ProjectDAO;
import com.realtrackandroid.backend.reminders.RemindersDAO;
import com.realtrackandroid.common.StyledButton;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.projects.Project;
import com.realtrackandroid.models.reminders.Reminders;
import com.realtrackandroid.views.dialogs.PickDateDialog;
import com.realtrackandroid.views.dialogs.PickDateDialogListener;
import com.realtrackandroid.views.dialogs.PickTimeDialog;
import com.realtrackandroid.views.dialogs.PickTimeDialogListener;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.HelpDialog;

/**
 * Add a new activity to an existing project
 */
public class AddActivitiesActivity extends SherlockFragmentActivity implements PickDateDialogListener, PickTimeDialogListener {
  protected int mYear, mMonth, mDay, mHour, mMinute, dayOfWeek;
  protected EditText title, startDate, endDate, notes, cohort, orgs, comms;
  protected EditText mondayTime, tuesdayTime, wednesdayTime, thursdayTime, fridayTime, saturdayTime, sundayTime;
  protected CheckBox mondayCheckbox, tuesdayCheckbox, wednesdayCheckbox, thursdayCheckbox, fridayCheckbox, saturdayCheckbox, sundayCheckbox;
  protected StyledButton submitButton;
  protected boolean startOrEnd; // used in OnDateSetListener to distinguish between start date and end date field
  protected String initiatives;
  protected int projectid;
  private long projectStartDate, projectEndDate;
  // used because we reuse the same listener for both fields
  protected Activities a;
  protected Reminders r;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_addactivities);

    // get the owner project
    projectid = getIntent().getExtras().getInt("projectid");
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    ProjectDAO pDao = new ProjectDAO(getApplicationContext());
    Project p = pDao.getProjectWithId(projectid);
    projectStartDate = p.getStartDate();
    projectEndDate = p.getEndDate();

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
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getSupportFragmentManager(), "timepicker");
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
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getSupportFragmentManager(), "timepicker");
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
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getSupportFragmentManager(), "timepicker");
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
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getSupportFragmentManager(), "timepicker");
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
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getSupportFragmentManager(), "timepicker");
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
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getSupportFragmentManager(), "timepicker");
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
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getSupportFragmentManager(), "timepicker");
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
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Bundle bundle = new Bundle();
        bundle.putLong("mindate", projectStartDate);
        try {
          Date date = parser.parse(endDate.getText().toString());
          Long maxDate = date.getTime() < projectEndDate ? date.getTime() : projectEndDate; // choose the lesser of the two for the upper bound
          bundle.putLong("maxdate", maxDate);
        } catch (ParseException e) {
          bundle.putLong("maxdate", projectEndDate);
        }
        try{
          Date date = parser.parse(startDate.getText().toString());
          bundle.putLong("displaydate", date.getTime()); // really only required in EditActivitiesActivity (which is a subclass of this one) for editing an activity
        } catch (ParseException e){
        }
        showDatePickerDialog(bundle);
      }

      private void showDatePickerDialog(Bundle bundle) {
        PickDateDialog pickDateDialog = new PickDateDialog();
        pickDateDialog.setArguments(bundle);
        pickDateDialog.show(getSupportFragmentManager(), "datepicker");
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
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Bundle bundle = new Bundle();
        bundle.putLong("maxdate", projectEndDate);
        try {
          Date date = parser.parse(startDate.getText().toString());
          Long minDate = date.getTime() > projectStartDate ? date.getTime() : projectStartDate; // choose the larger of the two for the lower bound
          bundle.putLong("mindate", minDate);
        } catch (ParseException e) {
          bundle.putLong("mindate", projectStartDate);
        }
        try {
          Date date = parser.parse(endDate.getText().toString());
          bundle.putLong("displaydate", date.getTime()); // really only required in EditActivitiesActivity (which is a subclass of this one) for editing an activity 
        } catch (ParseException e){
        }
        showDatePickerDialog(bundle);
      }

      private void showDatePickerDialog(Bundle bundle) {
        PickDateDialog pickDateDialog = new PickDateDialog();
        pickDateDialog.setArguments(bundle);
        pickDateDialog.show(getSupportFragmentManager(), "datepicker");
      }
    });

    title = (EditText) findViewById(R.id.title);
    notes = (EditText) findViewById(R.id.notes);
    cohort = (EditText) findViewById(R.id.cohort);
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

    submitButton = (StyledButton) findViewById(R.id.submitbutton);
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
          date.setHours(23);
          date.setMinutes(59);
          a.setEndDate(date.getTime());
        } catch (ParseException e) {
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage, Toast.LENGTH_SHORT).show();
          return;
        }

        // save title and other params
        a.setTitle(title.getText().toString());
        if (a.getTitle().equals("")) {
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage, Toast.LENGTH_SHORT).show();
          return;
        }

        a.setNotes(notes.getText().toString());
        a.setCohort(cohort.getText().toString());
        a.setOrgs(orgs.getText().toString());
        a.setComms(comms.getText().toString());

        // store initiatives in compact form "x|x|x" where the first x is WID, second is Youth etc
        // this order MUST match the DisplayActivitiesActivity.AllInits array
        // If x == 1, this activity has the corresponding initiative, if 0 then it doesn't.
        initiatives = (((CheckBox) findViewById(R.id.malariaCheckBox)).isChecked() ? "1" : "0") + "|" +
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
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  @Override
  public void setDate(String date) {
    if (startOrEnd)
      startDate.setText(date); //sets the chosen date in the text view
    else
      endDate.setText(date); //sets the chosen date in the text view
  }

  @Override
  public void setTime(String time) {
    switch (dayOfWeek) {
      case 1:
        mondayTime.setText(time); //sets the chosen date in the text view
        break;
      case 2:
        tuesdayTime.setText(time); //sets the chosen date in the text view
        break;
      case 3:
        wednesdayTime.setText(time); //sets the chosen date in the text view
        break;
      case 4:
        thursdayTime.setText(time); //sets the chosen date in the text view
        break;
      case 5:
        fridayTime.setText(time); //sets the chosen date in the text view
        break;
      case 6:
        saturdayTime.setText(time); //sets the chosen date in the text view
        break;
      case 7:
        sundayTime.setText(time); //sets the chosen date in the text view
        break;
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.addactivitymenu, menu);

    getSupportActionBar().setDisplayShowTitleEnabled(true);
    return true;
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }
}