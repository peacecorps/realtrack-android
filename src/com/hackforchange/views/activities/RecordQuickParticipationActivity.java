package com.hackforchange.views.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.models.activities.Participation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RecordQuickParticipationActivity extends SherlockActivity {
  static final int DATE_DIALOG = 0, TIME_DIALOG = 1;
  protected int mYear, mMonth, mDay, mHour, mMinute;
  private int largestParticipationId, activitiesId;
  protected Button submitButton;
  protected EditText menNumText, womenNumText, notesText;
  TextView date, time;
  protected CheckBox menCheckbox, womenCheckbox;
  protected Participation p;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recordquickparticipation);

    // read in the largest participation id yet recorded
    largestParticipationId = getIntent().getExtras().getInt("largestParticipationId");
    activitiesId = getIntent().getExtras().getInt("activitiesid");
  }

  @Override
  public void onResume() {
    super.onResume();
    Calendar c = Calendar.getInstance();

    final ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());
    p = pDao.getParticipationWithId(largestParticipationId);

    // display title for this activity
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(new ActivitiesDAO(getApplicationContext()).getActivityWithId(activitiesId).getTitle());

    // display date and time for this reminder

    date = (TextView) findViewById(R.id.date);
    date.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
                                             // not doing this means the first click simply focuses the text field
    date.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDialog(DATE_DIALOG);
      }
    });

    // entering the reminder time
    time = (TextView) findViewById(R.id.time);
    time.setFocusableInTouchMode(false); // do this so the time picker opens up on the very first selection of the text field
                                         // not doing this means the first click simply focuses the text field
    time.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          Bundle bundle = new Bundle();
          bundle.putString("timetodisplay", time.getText().toString());
          showDialog(TIME_DIALOG, bundle);
      }
    });

    // TODO: make sure men and women counts are not empty if the corresponding checkboxes are checked
    menCheckbox = (CheckBox) findViewById(R.id.menCheckBox);
    womenCheckbox = (CheckBox) findViewById(R.id.womenCheckBox);
    menNumText = (EditText) findViewById(R.id.numMen);
    womenNumText = (EditText) findViewById(R.id.numWomen);
    notesText = (EditText) findViewById(R.id.notes);
    submitButton = (Button) findViewById(R.id.submitbutton);

    menCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!menCheckbox.isChecked())
          menNumText.setText("");
      }
    });

    womenCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!womenCheckbox.isChecked())
          womenNumText.setText("");
      }
    });

    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Participation p = new Participation();

        p.setReminderid(0); // this field doesn't matter because we're setting serviced to true; it's here just for the not null constraint

        if(date.getText().length() == 0 || time.getText().length() == 0)
          return;


        DateFormat dateParser = new SimpleDateFormat("MM/dd/yyyy"); // example: 07/04/2013
        DateFormat timeParser = new SimpleDateFormat("hh:mm aaa"); // example: 07/04/2013
        try {
          Calendar c = Calendar.getInstance();
          // set date
          c.setTimeInMillis((dateParser.parse(date.getText().toString())).getTime());
          // set time
          Date date = timeParser.parse(time.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we constructed, we only extract the hour and
          // minute from the date object.
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          p.setDate(c.getTimeInMillis());
        } catch (ParseException e) {
        }

        p.setActivityid(activitiesId);

        // set men, women and serviced
        if (menCheckbox.isChecked()) {
          if (menNumText.getText().length() == 0)
            return;
          else
            p.setMen(Integer.parseInt(menNumText.getText().toString()));
        } else {
          p.setMen(0);
        }

        if (womenCheckbox.isChecked()) {
          if (womenNumText.getText().length() == 0)
            return;
          else
            p.setWomen(Integer.parseInt(womenNumText.getText().toString()));
        } else {
          p.setWomen(0);
        }

        p.setNotes(notesText.getText().toString());

        // update the serviced flag for this Reminder in the Reminders table
        // so that the next time the NotificationReceiver checks, this participation
        // does not show up as unserviced
        p.setServiced(true);

        pDao.addParticipation(p);

        finish();
      }
    });
  }

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

  // the callback received when the user "sets" the date in the dialog
  protected DatePickerDialog.OnDateSetListener mDateSetListener =
    new DatePickerDialog.OnDateSetListener() {
      public void onDateSet(DatePicker view, int year,
                            int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        date.setText(String.format("%02d/%02d/%4d", (mMonth + 1), mDay, mYear)); //sets the chosen date in the text view
        removeDialog(DATE_DIALOG); // remember to remove the dialog or onCreateDialog will NOT be called again! We need it to be called afresh
                                   // each time either startDate or endDate is clicked because we prepopulate the date picker with different
                                   // dates for startDate and endDate in EditProjectActivity.java's overriden onCreateDialog
                                   // http://stackoverflow.com/questions/2222648/change-the-contents-of-an-android-dialog-box-after-creation
      }
    };

  protected TimePickerDialog.OnTimeSetListener mTimeSetListener =
    new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        String timeToDisplay = String.format("%02d:%02d %s", ((mHour / 12) > 0 ? ((mHour == 12) ? 12 : (mHour - 12)) : ((mHour == 0) ? 12 : mHour)), mMinute, ((mHour / 12) > 0 ? "PM" : "AM"));
        time.setText(timeToDisplay); //sets the chosen date in the text view
        removeDialog(TIME_DIALOG); // remember to remove the dialog or onCreateDialog will NOT be called again! We need it to be called afresh
                                   // each time the reminder time field is clicked because we prepopulate the date picker with different
                                   // times in RecordQuickParticipationActivity.java's overriden onCreateDialog
                                   // http://stackoverflow.com/questions/2222648/change-the-contents-of-an-android-dialog-box-after-creation
      }
    };
}