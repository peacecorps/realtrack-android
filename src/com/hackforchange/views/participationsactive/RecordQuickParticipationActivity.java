package com.hackforchange.views.participationsactive;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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
import com.hackforchange.backend.activities.ParticipantDAO;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.activities.Participant;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.views.dialogs.PickDateDialog;
import com.hackforchange.views.dialogs.PickDateDialogListener;
import com.hackforchange.views.dialogs.PickTimeDialog;
import com.hackforchange.views.dialogs.PickTimeDialogListener;
import com.hackforchange.views.participationsactive.signinsheet.SignInSheetLandingActivity;

public class RecordQuickParticipationActivity extends SherlockFragmentActivity implements
PickDateDialogListener, PickTimeDialogListener {
  static final int ADD_PARTICIPANTS_REQUEST = 1;

  protected int mYear, mMonth, mDay, mHour, mMinute;

  private int activitiesId;

  protected Button submitButton;

  protected Button signinSheetButton;

  protected EditText menUnder15NumText, men1524NumText, menOver24NumText, womenUnder15NumText, women1524NumText,
  womenOver24NumText, notesText;

  TextView date, time;

  protected CheckBox menUnder15Checkbox, men1524Checkbox, menOver24Checkbox, womenUnder15Checkbox,
  women1524Checkbox, womenOver24Checkbox;

  private Activities a;
  
  private List <Participant> participantList;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recordquickparticipation);

    // read in the largest participation id yet recorded
    activitiesId = getIntent().getExtras().getInt("activitiesid");
    
    participantList = new ArrayList<Participant>();
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    final ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());
    final ParticipantDAO participantDao = new ParticipantDAO(getApplicationContext());

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

    // opening the sign-in sheet
    signinSheetButton  = (Button) findViewById(R.id.openSigninSheetButton);
    signinSheetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(), SignInSheetLandingActivity.class);
        startActivityForResult(i, ADD_PARTICIPANTS_REQUEST);
        overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
      }
    });

    menUnder15Checkbox = (CheckBox) findViewById(R.id.menCheckBox);
    men1524Checkbox = (CheckBox) findViewById(R.id.men1524CheckBox);
    menOver24Checkbox = (CheckBox) findViewById(R.id.menOver24CheckBox);
    womenUnder15Checkbox = (CheckBox) findViewById(R.id.womenCheckBox);
    women1524Checkbox = (CheckBox) findViewById(R.id.women1524CheckBox);
    womenOver24Checkbox = (CheckBox) findViewById(R.id.womenOver24CheckBox);
    menUnder15NumText = (EditText) findViewById(R.id.numMen);
    men1524NumText = (EditText) findViewById(R.id.numMen1524);
    menOver24NumText = (EditText) findViewById(R.id.numMenOver24);
    womenUnder15NumText = (EditText) findViewById(R.id.numWomen);
    women1524NumText = (EditText) findViewById(R.id.numWomen1524);
    womenOver24NumText = (EditText) findViewById(R.id.numWomenOver24);
    notesText = (EditText) findViewById(R.id.notes);
    submitButton = (Button) findViewById(R.id.submitbutton);

    menUnder15Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!menUnder15Checkbox.isChecked())
          menUnder15NumText.setText("");
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

    womenUnder15Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!womenUnder15Checkbox.isChecked())
          womenUnder15NumText.setText("");
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

        if (!menUnder15Checkbox.isChecked() && !men1524Checkbox.isChecked()
                && !menOver24Checkbox.isChecked() && !womenUnder15Checkbox.isChecked()
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
        if (menUnder15Checkbox.isChecked()) {
          if (menUnder15NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setMen(Integer.parseInt(menUnder15NumText.getText().toString()));
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

        if (womenUnder15Checkbox.isChecked()) {
          if (womenUnder15NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setWomen(Integer.parseInt(womenUnder15NumText.getText().toString()));
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

        int participationId = pDao.addParticipation(p);
        
        // write the participant information
        // first add the participation id (we have to wait till now because
        // the participation id is only assigned after the participation
        // has been added to its own table)
        for(Participant participant: participantList){
          participant.setParticipationid(participationId);
        }
        
        participantDao.addParticipants(participantList);

        finish();
      }

    });
  }

  // Handle participant list returned by SignInSheetLandingActivity
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == ADD_PARTICIPANTS_REQUEST) {
      if (resultCode == RESULT_OK) {
        //TODO: 
        Bundle resultBundle = intent.getExtras();
        List<Participant> currentParticipantList = resultBundle.getParcelableArrayList("participantList");
        
        participantList.addAll(currentParticipantList);
        
        if(!participantList.isEmpty())
          updateParticipantNumbersInDisplay();
      }
    }
  }

  private void updateParticipantNumbersInDisplay() {
    int menUnder15 = 0;
    int men1524 = 0;
    int menOver24 = 0;
    int womenUnder15 = 0;
    int women1524 = 0;
    int womenOver24 = 0;
    
    for(Participant p: participantList){
      if(p.getGender()==Participant.MALE){
        if(p.getAge()<15)
          menUnder15++;
        else if(p.getAge()<25)
          men1524++;
        else
          menOver24++;
      }
      else if(p.getGender()==Participant.FEMALE){ //could simply be an else but just being cautious and making sure the value is FEMALE
        if(p.getAge()<15)
          womenUnder15++;
        else if(p.getAge()<25)
          women1524++;
        else
          womenOver24++;
      }
    }
      
    // set filters on the text fields so the PCV cannot manually enter a number less than the 
    // current number of participants. Note that even though we reinitialize menUnder15, men1524
    // etc to 0 in this method, there is no way their values can be less than what they were because
    // there is no way that a participant once submitted via the sign-in sheet can be removed.
    menUnder15NumText.setText(Integer.toString(menUnder15));
    menUnder15NumText.setFilters(new InputFilter[]{new MinNumMenWomenInputFilter(menUnder15)});
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if(menUnder15>0){
      menUnder15Checkbox.setChecked(true);
      menUnder15Checkbox.setEnabled(false);
    }
    
    men1524NumText.setText(Integer.toString(men1524));
    men1524NumText.setFilters(new InputFilter[]{new MinNumMenWomenInputFilter(men1524)});
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if(men1524>0){
      men1524Checkbox.setChecked(true);
      men1524Checkbox.setEnabled(false);
    }
    
    menOver24NumText.setText(Integer.toString(menOver24));
    menOver24NumText.setFilters(new InputFilter[]{new MinNumMenWomenInputFilter(menOver24)});
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if(menOver24>0){
      menOver24Checkbox.setChecked(true);
      menOver24Checkbox.setEnabled(false);
    }
    
    womenUnder15NumText.setText(Integer.toString(womenUnder15));
    womenUnder15NumText.setFilters(new InputFilter[]{new MinNumMenWomenInputFilter(womenUnder15)});
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if(womenUnder15>0){
      womenUnder15Checkbox.setChecked(true);
      womenUnder15Checkbox.setEnabled(false);
    }
    
    women1524NumText.setText(Integer.toString(women1524));
    women1524NumText.setFilters(new InputFilter[]{new MinNumMenWomenInputFilter(women1524)});
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if(women1524>0){
      women1524Checkbox.setChecked(true);
      women1524Checkbox.setEnabled(false);
    }
    
    womenOver24NumText.setText(Integer.toString(womenOver24));
    womenOver24NumText.setFilters(new InputFilter[]{new MinNumMenWomenInputFilter(womenOver24)});
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if(womenOver24>0){
      womenOver24Checkbox.setChecked(true);
      womenOver24Checkbox.setEnabled(false);
    }
    
    signinSheetButton.setText(getResources().getString(R.string.openSigninSheetButtonLabel)+" (currently has "+participantList.size()+" participant(s))");
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