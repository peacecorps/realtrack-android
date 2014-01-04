package com.hackforchange.views.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.views.dialogs.PickDateDialog;
import com.hackforchange.views.dialogs.PickDateDialogListener;
import com.hackforchange.views.dialogs.PickTimeDialog;
import com.hackforchange.views.dialogs.PickTimeDialogListener;

public class RecordQuickParticipationActivity extends SherlockFragmentActivity implements
        PickDateDialogListener, PickTimeDialogListener {
  protected int mYear, mMonth, mDay, mHour, mMinute;

  private int activitiesId;

  protected Button submitButton;

  protected EditText menNumText, men1524NumText, menOver24NumText, womenNumText, women1524NumText,
          womenOver24NumText, notesText;

  TextView date, time;

  protected CheckBox menCheckbox, men1524Checkbox, menOver24Checkbox, womenCheckbox,
          women1524Checkbox, womenOver24Checkbox;

  private Activities a;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recordquickparticipation);

    // read in the largest participation id yet recorded
    activitiesId = getIntent().getExtras().getInt("activitiesid");
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    final ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());

    a = new ActivitiesDAO(getApplicationContext()).getActivityWithId(activitiesId);

    // display title for this activity
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(a.getTitle());

    // display date and time for this reminder

    date = (TextView) findViewById(R.id.date);
    date.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first
                                         // selection of the text field
    // not doing this means the first click simply focuses the text field
    date.setFocusable(false);
    date.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong("mindate", a.getStartDate());
        bundle.putLong("maxdate", a.getEndDate());
        showDatePickerDialog(bundle);
      }

      private void showDatePickerDialog(Bundle bundle) {
        PickDateDialog pickDateDialog = new PickDateDialog();
        pickDateDialog.setArguments(bundle);
        pickDateDialog.show(getSupportFragmentManager(), "datepicker");
      }
    });

    // entering the reminder time
    time = (TextView) findViewById(R.id.time);
    time.setFocusableInTouchMode(false); // do this so the time picker opens up on the very first
                                         // selection of the text field
    // not doing this means the first click simply focuses the text field
    time.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("timetodisplay", time.getText().toString());
        showTimePickerDialog(bundle);
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getSupportFragmentManager(), "timepicker");
      }
    });

    menCheckbox = (CheckBox) findViewById(R.id.menCheckBox);
    men1524Checkbox = (CheckBox) findViewById(R.id.men1524CheckBox);
    menOver24Checkbox = (CheckBox) findViewById(R.id.menOver24CheckBox);
    womenCheckbox = (CheckBox) findViewById(R.id.womenCheckBox);
    women1524Checkbox = (CheckBox) findViewById(R.id.women1524CheckBox);
    womenOver24Checkbox = (CheckBox) findViewById(R.id.womenOver24CheckBox);
    menNumText = (EditText) findViewById(R.id.numMen);
    men1524NumText = (EditText) findViewById(R.id.numMen1524);
    menOver24NumText = (EditText) findViewById(R.id.numMenOver24);
    womenNumText = (EditText) findViewById(R.id.numWomen);
    women1524NumText = (EditText) findViewById(R.id.numWomen1524);
    womenOver24NumText = (EditText) findViewById(R.id.numWomenOver24);
    notesText = (EditText) findViewById(R.id.notes);
    submitButton = (Button) findViewById(R.id.submitbutton);

    menCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!menCheckbox.isChecked())
          menNumText.setText("");
      }
    });

    men1524Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!men1524Checkbox.isChecked())
          men1524NumText.setText("");
      }
    });

    menOver24Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!menOver24Checkbox.isChecked())
          menOver24NumText.setText("");
      }
    });

    womenCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!womenCheckbox.isChecked())
          womenNumText.setText("");
      }
    });

    women1524Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!women1524Checkbox.isChecked())
          women1524NumText.setText("");
      }
    });

    womenOver24Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!womenOver24Checkbox.isChecked())
          womenOver24NumText.setText("");
      }
    });

    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (!menCheckbox.isChecked() && !men1524Checkbox.isChecked()
                && !menOver24Checkbox.isChecked() && !womenCheckbox.isChecked()
                && !women1524Checkbox.isChecked() && !womenOver24Checkbox.isChecked()) {
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage,
                  Toast.LENGTH_SHORT).show();
          return;
        }

        Participation p = new Participation();

        p.setReminderid(0); // this field doesn't matter because we're setting serviced to true;
                            // it's here just for the not null constraint

        DateFormat dateParser = new SimpleDateFormat("MM/dd/yyyy"); // example: 07/04/2013
        DateFormat timeParser = new SimpleDateFormat("hh:mm aaa"); // example: 07/04/2013
        try {
          Calendar c = Calendar.getInstance();
          // set date
          c.setTimeInMillis((dateParser.parse(date.getText().toString())).getTime());
          // set time
          Date date = timeParser.parse(time.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the
          // hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us
          // (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we constructed, we only extract
          // the hour and
          // minute from the date object.
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          p.setDate(c.getTimeInMillis());
        }
        catch (ParseException e) {
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage, Toast.LENGTH_SHORT).show();
          return;
        }

        p.setActivityid(activitiesId);

        // set men, women and serviced
        if (menCheckbox.isChecked()) {
          if (menNumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setMen(Integer.parseInt(menNumText.getText().toString()));
        }
        else {
          p.setMen(0);
        }

        if (men1524Checkbox.isChecked()) {
          if (men1524NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setMen1524(Integer.parseInt(men1524NumText.getText().toString()));
        }
        else {
          p.setMen1524(0);
        }

        if (menOver24Checkbox.isChecked()) {
          if (menOver24NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setMenOver24(Integer.parseInt(menOver24NumText.getText().toString()));
        }
        else {
          p.setMenOver24(0);
        }

        if (womenCheckbox.isChecked()) {
          if (womenNumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setWomen(Integer.parseInt(womenNumText.getText().toString()));
        }
        else {
          p.setWomen(0);
        }

        if (women1524Checkbox.isChecked()) {
          if (women1524NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setWomen1524(Integer.parseInt(women1524NumText.getText().toString()));
        }
        else {
          p.setWomen1524(0);
        }

        if (womenOver24Checkbox.isChecked()) {
          if (womenOver24NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setWomenOver24(Integer.parseInt(womenOver24NumText.getText().toString()));
        }
        else {
          p.setWomenOver24(0);
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
  public boolean onCreateOptionsMenu(Menu menu) {
    getSupportActionBar().setDisplayShowTitleEnabled(true);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // provide a back button on the actionbar
        finish();
        break;
    }

    return true;
  }

  @Override
  public void setDate(String selectedDate) {
    date.setText(selectedDate);
  }

  @Override
  public void setTime(String selectedTime) {
    time.setText(selectedTime);
  }
}