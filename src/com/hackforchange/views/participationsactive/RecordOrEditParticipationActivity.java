package com.hackforchange.views.participationsactive;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.activities.ParticipantDAO;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.common.StyledButton;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.activities.Participant;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.views.participationsactive.signinsheet.SignInSheetLandingActivity;

/**
 * RecordOrEditParticipationActivity is different from RecordQuickParticipationActivity in the following ways:
 * 1. RecordOrEditParticipationActivity ALREADY has a participation associated with it (created when
 *    reminders are served (in NotificationService)). RecordQuickParticipationActivity does not have a pre-existing
 *    participation. It has to create one.
 * 2. Because RecordOrEditParticipationActivity serves an existing participation, it has a date and time associated with
 *    it. RecordQuickParticipationActivity does not have a date and time a priori, and must get these from the user.
 * @author Raj
 */
public class RecordOrEditParticipationActivity extends SherlockActivity {
  static final int ADD_PARTICIPANTS_REQUEST = 1;

  private int participationId;
  private long dateTime;
  protected StyledButton dismissButton;
  private StyledButton submitButton;
  private StyledButton signinSheetButton;
  protected EditText menUnder15NumText, men1524NumText, menOver24NumText, womenUnder15NumText, women1524NumText, womenOver24NumText, notesText;
  protected CheckBox menUnder15Checkbox, men1524Checkbox, menOver24Checkbox, womenUnder15Checkbox, women1524Checkbox, womenOver24Checkbox;
  protected Participation p;
  private ArrayList<Participant> participantList;
  
  private int menUnder15ManuallyEntered, men1524ManuallyEntered, menOver24ManuallyEntered, womenUnder15ManuallyEntered, women1524ManuallyEntered, womenOver24ManuallyEntered;
  private int menUnder15FromSignInSheet, men1524FromSignInSheet, menOver24FromSignInSheet, womenUnder15FromSignInSheet, women1524FromSignInSheet, womenOver24FromSignInSheet;
  
  private boolean editParticipation; //true if coming in from ProjectsActivitiesListAdapter

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recordoreditparticipation);

    // read in the ID of the activities for which we're recording participation
    participationId = getIntent().getExtras().getInt("participationid");

    // also note the date and time
    dateTime = getIntent().getExtras().getLong("datetime");
    
    editParticipation = getIntent().getExtras().getBoolean("editparticipation");

    if(!editParticipation){
      participantList = new ArrayList<Participant>();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(dateTime);

    final ParticipationDAO participationDao = new ParticipationDAO(getApplicationContext());
    p = participationDao.getParticipationWithId(participationId);
    
    final ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
    final Activities a = aDao.getActivityWithId(p.getActivityid());
    DateFormat simpleDateParser = new SimpleDateFormat("MM/dd/yyyy");
    final String participationDate = simpleDateParser.format(c.getTime());

    final ParticipantDAO participantDao = new ParticipantDAO(getApplicationContext());

    // display title for this activity
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(new ActivitiesDAO(getApplicationContext()).getActivityWithId(p.getActivityid()).getTitle());

    // display date and time for this reminder
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy, EEEE, hh:mm aaa"); // example: 07/04/2013, Thursday, 6:13 PM
    TextView datetime = (TextView) findViewById(R.id.datetime);
    datetime.setText(parser.format(c.getTime()));

    // opening the sign-in sheet
    signinSheetButton  = (StyledButton) findViewById(R.id.openSigninSheetButton);
    signinSheetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(), SignInSheetLandingActivity.class);
        i.putExtra("activitytitle", a.getTitle()); // displayed on SignInSheetActivity
        i.putExtra("participationdate", participationDate); // displayed on SignInSheetActivity
        Bundle resultBundle = new Bundle();
        resultBundle.putParcelableArrayList("participantList", participantList);
        i.putExtras(resultBundle);
        i.putExtra("firstOpen", true); //used to jump straight to SignInSheetActivity the very first time
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
    submitButton = (StyledButton) findViewById(R.id.submitbutton);
    dismissButton = (StyledButton) findViewById(R.id.dismissButton);
    
    if(editParticipation){
      this.setTitle(getResources().getString(R.string.editparticipationactivity_label));
      participantList = participantDao.getAllParticipantsForParticipationId(participationId);
      ((View) findViewById(R.id.spacer)).setVisibility(View.GONE);
      notesText.setText(p.getNotes());
      dismissButton.setVisibility(View.GONE);
    }
    
    updateParticipantNumbersInDisplay();

    menUnder15Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!menUnder15Checkbox.isChecked())
          menUnder15NumText.setText("");
      }
    });
    
    menUnder15NumText.addTextChangedListener(new AbstractTextValidator(menUnder15NumText) {
      @Override
      public void validate(EditText editText) {
        String enteredValue = editText.getText().toString();
        if(enteredValue.length()==0 || Integer.parseInt(enteredValue)<menUnder15FromSignInSheet){
          editText.setText(Integer.toString(menUnder15FromSignInSheet));
          return;
        }
        if(Integer.parseInt(enteredValue)!=menUnder15FromSignInSheet)
          menUnder15ManuallyEntered = Integer.parseInt(enteredValue)-menUnder15FromSignInSheet;
      }
    });

    men1524Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!men1524Checkbox.isChecked())
          men1524NumText.setText("");
      }
    });
    
    men1524NumText.addTextChangedListener(new AbstractTextValidator(men1524NumText) {
      @Override
      public void validate(EditText editText) {
        String enteredValue = editText.getText().toString();
        if(enteredValue.length()==0 || Integer.parseInt(enteredValue)<men1524FromSignInSheet){
          editText.setText(Integer.toString(men1524FromSignInSheet));
          return;
        }
        men1524ManuallyEntered = Integer.parseInt(enteredValue)-men1524FromSignInSheet;
      }
    });

    menOver24Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!menOver24Checkbox.isChecked())
          menOver24NumText.setText("");
      }
    });
    
    menOver24NumText.addTextChangedListener(new AbstractTextValidator(menOver24NumText) {
      @Override
      public void validate(EditText editText) {
        String enteredValue = editText.getText().toString();
        if(enteredValue.length()==0 || Integer.parseInt(enteredValue)<menOver24FromSignInSheet){
          editText.setText(Integer.toString(menOver24FromSignInSheet));
          return;
        }
        menOver24ManuallyEntered = Integer.parseInt(enteredValue)-menOver24FromSignInSheet;
      }
    });

    womenUnder15Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!womenUnder15Checkbox.isChecked())
          womenUnder15NumText.setText("");
      }
    });

    womenUnder15NumText.addTextChangedListener(new AbstractTextValidator(womenUnder15NumText) {
      @Override
      public void validate(EditText editText) {
        String enteredValue = editText.getText().toString();
        if(enteredValue.length()==0 || Integer.parseInt(enteredValue)<womenUnder15FromSignInSheet){
          editText.setText(Integer.toString(womenUnder15FromSignInSheet));
          return;
        }
        womenUnder15ManuallyEntered = Integer.parseInt(enteredValue)-womenUnder15FromSignInSheet;
      }
    });
    
    women1524Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!women1524Checkbox.isChecked())
          women1524NumText.setText("");
      }
    });
    
    women1524NumText.addTextChangedListener(new AbstractTextValidator(women1524NumText) {
      @Override
      public void validate(EditText editText) {
        String enteredValue = editText.getText().toString();
        if(enteredValue.length()==0 || Integer.parseInt(enteredValue)<women1524FromSignInSheet){
          editText.setText(Integer.toString(women1524FromSignInSheet));
          return;
        }
        women1524ManuallyEntered = Integer.parseInt(enteredValue)-women1524FromSignInSheet;
      }
    });

    womenOver24Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!womenOver24Checkbox.isChecked())
          womenOver24NumText.setText("");
      }
    });
    
    womenOver24NumText.addTextChangedListener(new AbstractTextValidator(womenOver24NumText) {
      @Override
      public void validate(EditText editText) {
        String enteredValue = editText.getText().toString();
        if(enteredValue.length()==0 || Integer.parseInt(enteredValue)<womenOver24FromSignInSheet){
          editText.setText(Integer.toString(womenOver24FromSignInSheet));
          return;
        }
        womenOver24ManuallyEntered = Integer.parseInt(enteredValue)-womenOver24FromSignInSheet;
      }
    });

    dismissButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        participationDao.deleteParticipation(participationId);
        finish();
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

        // set men, women and serviced
        if (menUnder15Checkbox.isChecked()) {
          if (menUnder15NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setMenUnder15(Integer.parseInt(menUnder15NumText.getText().toString()));
        }
        else {
          p.setMenUnder15(0);
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
            p.setWomenUnder15(Integer.parseInt(womenUnder15NumText.getText().toString()));
        }
        else {
          p.setWomenUnder15(0);
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

        // write the participant information
        // first add the participation id (we don't really have to wait until now as we did in
        // RecordQuickParticipationActivity but we do it all the same to keep things uniform between
        // both the classes)
        for(int i=0;i<participantList.size();++i){
          Participant participant = participantList.get(i);
          if(participant.getId()!=-1){ //the -1 indicates this is a participant not already in the database
            participantList.remove(i--);
          }
          else{
            participant.setParticipationId(participationId);
          }
        }
        
        participantDao.addParticipants(participantList); // the -1 we set into the id won't affect the actual database write
                                                         // because we ignore the id field of the participant object there

        participationDao.updateParticipation(p);

        finish();
      }
    });
  }

  //Handle participant list returned by SignInSheetLandingActivity
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == ADD_PARTICIPANTS_REQUEST) {
      if (resultCode == RESULT_OK) {
        Bundle resultBundle = intent.getExtras();
        participantList = resultBundle.getParcelableArrayList("participantList");
        updateParticipantNumbersInDisplay();
      }
    }
  }

  private void updateParticipantNumbersInDisplay() {
    menUnder15FromSignInSheet = 0;
    men1524FromSignInSheet = 0;
    menOver24FromSignInSheet = 0;
    womenUnder15FromSignInSheet = 0;
    women1524FromSignInSheet = 0;
    womenOver24FromSignInSheet = 0;
    
    for(Participant p: participantList){
      if(p.getGender()==Participant.MALE){
        if(p.getAge()<15)
          menUnder15FromSignInSheet++;
        else if(p.getAge()<25)
          men1524FromSignInSheet++;
        else
          menOver24FromSignInSheet++;
      }
      else if(p.getGender()==Participant.FEMALE){ //could simply be an else but just being cautious and making sure the value is FEMALE
        if(p.getAge()<15)
          womenUnder15FromSignInSheet++;
        else if(p.getAge()<25)
          women1524FromSignInSheet++;
        else
          womenOver24FromSignInSheet++;
      }
    }
    
    if(editParticipation){
      menUnder15ManuallyEntered = p.getMenUnder15() - menUnder15FromSignInSheet;
      men1524ManuallyEntered = p.getMen1524() - men1524FromSignInSheet;
      menOver24ManuallyEntered = p.getMenOver24() - menOver24FromSignInSheet;
      womenUnder15ManuallyEntered = p.getWomenUnder15() - womenUnder15FromSignInSheet;
      women1524ManuallyEntered = p.getWomen1524() - women1524FromSignInSheet;
      womenOver24ManuallyEntered = p.getWomenOver24() - womenOver24FromSignInSheet;
      editParticipation = false; //makes sure we only do the above once
    }
      
    // Note that even though we reinitialize menUnder15, men1524
    // etc to 0 in this method, there is no way their values can be less than what they were the last time around
    // there is no way that a participant once submitted via the sign-in sheet can be removed i.e., these values
    // are strictly increasing.
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalMenUnder15 = menUnder15FromSignInSheet+menUnder15ManuallyEntered;
    if(totalMenUnder15>0){
      menUnder15NumText.setText(Integer.toString(totalMenUnder15));
      menUnder15Checkbox.setChecked(true);
      menUnder15Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalMen1524 = men1524FromSignInSheet+men1524ManuallyEntered;
    if(totalMen1524>0){
      men1524NumText.setText(Integer.toString(totalMen1524));
      men1524Checkbox.setChecked(true);
      men1524Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalMenOver24 = menOver24FromSignInSheet+menOver24ManuallyEntered;
    if(totalMenOver24>0){
      menOver24NumText.setText(Integer.toString(totalMenOver24));
      menOver24Checkbox.setChecked(true);
      menOver24Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalWomenUnder15 = womenUnder15FromSignInSheet+womenUnder15ManuallyEntered;
    if(totalWomenUnder15>0){
      womenUnder15NumText.setText(Integer.toString(totalWomenUnder15));
      womenUnder15Checkbox.setChecked(true);
      womenUnder15Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalWomen1524 = women1524FromSignInSheet+women1524ManuallyEntered;
    if(totalWomen1524>0){
      women1524NumText.setText(Integer.toString(totalWomen1524));
      women1524Checkbox.setChecked(true);
      women1524Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalWomenOver24 = womenOver24FromSignInSheet+womenOver24ManuallyEntered;
    if(totalWomenOver24>0){
      womenOver24NumText.setText(Integer.toString(totalWomenOver24));
      womenOver24Checkbox.setChecked(true);
      womenOver24Checkbox.setEnabled(false);
    }

    signinSheetButton.setText(getResources().getString(R.string.openSigninSheetButtonLabel)+" (currently has "+participantList.size()+" participant(s))");
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }
}