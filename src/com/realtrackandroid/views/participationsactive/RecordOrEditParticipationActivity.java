package com.realtrackandroid.views.participationsactive;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ActivitiesDAO;
import com.realtrackandroid.backend.activities.ParticipantDAO;
import com.realtrackandroid.backend.activities.ParticipationDAO;
import com.realtrackandroid.common.StyledButton;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.activities.Participant;
import com.realtrackandroid.models.activities.Participation;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.HelpDialog;
import com.realtrackandroid.views.participationsactive.signinsheet.SignInSheetLandingActivity;

/**
 * RecordOrEditParticipationActivity is different from RecordQuickParticipationActivity in the following ways:
 * 1. RecordOrEditParticipationActivity ALREADY has a participation associated with it (created when
 *    reminders are served (in NotificationService)). RecordQuickParticipationActivity does not have a pre-existing
 *    participation. It has to create one.
 * 2. Because RecordOrEditParticipationActivity serves an existing participation, it has a date and time associated with
 *    it. RecordQuickParticipationActivity does not have a date and time a priori, and must get these from the user.
 * @author Raj
 */
public class RecordOrEditParticipationActivity extends SherlockFragmentActivity {
  static final int ADD_PARTICIPANTS_REQUEST = 1;

  private int participationId;
  private long dateTime;
  protected StyledButton dismissButton;
  private StyledButton submitButton;
  private StyledButton signinSheetButton;
  protected Participation p;
  private ArrayList<Participant> participantList;

  protected EditText men09NumText, men1017NumText, men1824NumText, menOver25NumText,
                     women09NumText, women1017NumText, women1824NumText, womenOver25NumText, notesText;
  
  protected EditText spmen09NumText, spmen1017NumText, spmen1824NumText, spmenOver25NumText,
                     spwomen09NumText, spwomen1017NumText, spwomen1824NumText, spwomenOver25NumText,
                     spnotesText;

  protected CheckBox men09Checkbox, men1017Checkbox, men1824Checkbox, menOver25Checkbox,
                     women09Checkbox, women1017Checkbox, women1824Checkbox, womenOver25Checkbox;

  protected CheckBox spmen09Checkbox, spmen1017Checkbox, spmen1824Checkbox, spmenOver25Checkbox,
                     spwomen09Checkbox, spwomen1017Checkbox, spwomen1824Checkbox, spwomenOver25Checkbox;
  
  private int men09ManuallyEntered, men1017ManuallyEntered, men1824ManuallyEntered, menOver25ManuallyEntered,
              women09ManuallyEntered, women1017ManuallyEntered, women1824ManuallyEntered, womenOver25ManuallyEntered;
  
  private int men09FromSignInSheet, men1017FromSignInSheet, men1824FromSignInSheet, menOver25FromSignInSheet, 
              women09FromSignInSheet, women1017FromSignInSheet, women1824FromSignInSheet, womenOver25FromSignInSheet;
  
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
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(dateTime);

    final ParticipationDAO participationDao = new ParticipationDAO(getApplicationContext());
    p = participationDao.getParticipationWithId(participationId);
    
    final ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
    final Activities a = aDao.getActivityWithId(p.getActivityid());
    DateFormat simpleDateParser = new SimpleDateFormat("MM/dd/yyyy");
    final String participationDate = simpleDateParser.format(cal.getTime());

    final ParticipantDAO participantDao = new ParticipantDAO(getApplicationContext());

    // display title for this activity
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(new ActivitiesDAO(getApplicationContext()).getActivityWithId(p.getActivityid()).getTitle());

    // display date and time for this reminder
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy, EEEE, hh:mm aaa"); // example: 07/04/2013, Thursday, 6:13 PM
    TextView datetime = (TextView) findViewById(R.id.datetime);
    datetime.setText(parser.format(cal.getTime()));

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

    men09Checkbox = (CheckBox) findViewById(R.id.men09CheckBox);
    men1017Checkbox = (CheckBox) findViewById(R.id.men1017CheckBox);
    men1824Checkbox = (CheckBox) findViewById(R.id.men1824CheckBox);
    menOver25Checkbox = (CheckBox) findViewById(R.id.menOver25CheckBox);
    women09Checkbox = (CheckBox) findViewById(R.id.women09CheckBox);
    women1017Checkbox = (CheckBox) findViewById(R.id.women1017CheckBox);
    women1824Checkbox = (CheckBox) findViewById(R.id.women1824CheckBox);
    womenOver25Checkbox = (CheckBox) findViewById(R.id.womenOver25CheckBox);
    men09NumText = (EditText) findViewById(R.id.numMen09);
    men1017NumText = (EditText) findViewById(R.id.numMen1017);
    men1824NumText = (EditText) findViewById(R.id.numMen1824);
    menOver25NumText = (EditText) findViewById(R.id.numMenOver25);
    women09NumText = (EditText) findViewById(R.id.numWomen09);
    women1017NumText = (EditText) findViewById(R.id.numWomen1017);
    women1824NumText = (EditText) findViewById(R.id.numWomen1824);
    womenOver25NumText = (EditText) findViewById(R.id.numWomenOver25);
    
    spmen09Checkbox = (CheckBox) findViewById(R.id.spmen09CheckBox);
    spmen1017Checkbox = (CheckBox) findViewById(R.id.spmen1017CheckBox);
    spmen1824Checkbox = (CheckBox) findViewById(R.id.spmen1824CheckBox);
    spmenOver25Checkbox = (CheckBox) findViewById(R.id.spmenOver25CheckBox);
    spwomen09Checkbox = (CheckBox) findViewById(R.id.spwomen09CheckBox);
    spwomen1017Checkbox = (CheckBox) findViewById(R.id.spwomen1017CheckBox);
    spwomen1824Checkbox = (CheckBox) findViewById(R.id.spwomen1824CheckBox);
    spwomenOver25Checkbox = (CheckBox) findViewById(R.id.spwomenOver25CheckBox);
    spmen09NumText = (EditText) findViewById(R.id.numSpMen09);
    spmen1017NumText = (EditText) findViewById(R.id.numSpMen1017);
    spmen1824NumText = (EditText) findViewById(R.id.numSpMen1824);
    spmenOver25NumText = (EditText) findViewById(R.id.numSpMenOver25);
    spwomen09NumText = (EditText) findViewById(R.id.numSpWomen09);
    spwomen1017NumText = (EditText) findViewById(R.id.numSpWomen1017);
    spwomen1824NumText = (EditText) findViewById(R.id.numSpWomen1824);
    spwomenOver25NumText = (EditText) findViewById(R.id.numSpWomenOver25);
    
    notesText = (EditText) findViewById(R.id.notes);
    submitButton = (StyledButton) findViewById(R.id.submitbutton);
    dismissButton = (StyledButton) findViewById(R.id.dismissButton);
    
    if(editParticipation){
      this.setTitle(getResources().getString(R.string.editparticipationactivity_label));
      participantList = participantDao.getAllParticipantsForParticipationId(participationId);
      ((View) findViewById(R.id.spacer)).setVisibility(View.GONE);
      notesText.setText(p.getNotes());
      updateServiceProviderCounts();
      dismissButton.setVisibility(View.GONE);
    }
    
    updateParticipantCounts();
    
    CheckBox[] checkBoxArray = {men09Checkbox, men1017Checkbox, men1824Checkbox, menOver25Checkbox,
                                women09Checkbox, women1017Checkbox, women1824Checkbox, womenOver25Checkbox, 
                                spmen09Checkbox, spmen1017Checkbox, spmen1824Checkbox, spmenOver25Checkbox,
                                spwomen09Checkbox, spwomen1017Checkbox, spwomen1824Checkbox, spwomenOver25Checkbox};

    EditText[] numTextArray = {men09NumText, men1017NumText, men1824NumText, menOver25NumText,
                               women09NumText, women1017NumText, women1824NumText, womenOver25NumText,
                               spmen09NumText, spmen1017NumText, spmen1824NumText, spmenOver25NumText,
                               spwomen09NumText, spwomen1017NumText, spwomen1824NumText, spwomenOver25NumText};
    
    for (int i=0; i<checkBoxArray.length; ++i){
      final CheckBox c = checkBoxArray[i];
      final EditText e = numTextArray[i];
      c.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!c.isChecked()){
            e.setEnabled(false);
            e.setText("");
          }
          else{
            e.setEnabled(true);
          }
        }
      });
    }
    
    dismissButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        participationDao.deleteParticipation(participationId);
        finish();
      }
    });

    submitButton.setOnClickListener(new View.OnClickListener() {
      private boolean errorsFound;
      
      @Override
      public void onClick(View v) {
        errorsFound = false;
        
        if (!men09Checkbox.isChecked() && !men1017Checkbox.isChecked() && !men1824Checkbox.isChecked()
                && !menOver25Checkbox.isChecked() && !women09Checkbox.isChecked() && !women1017Checkbox.isChecked()
                && !women1824Checkbox.isChecked() && !womenOver25Checkbox.isChecked()) {
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage,
                  Toast.LENGTH_SHORT).show();
          return;
        }

        // set men, women and serviced
        if (men09Checkbox.isChecked()) {
          if (men09NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            checkEnteredValueNotLessThanSigninSheetValue(men09NumText, men09FromSignInSheet);
            p.setMen09(Integer.parseInt(men09NumText.getText().toString()));
          }
        }
        else {
          p.setMen09(0);
        }
        
        if (men1017Checkbox.isChecked()) {
          if (men1017NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            checkEnteredValueNotLessThanSigninSheetValue(men1017NumText, men1017FromSignInSheet);
            p.setMen1017(Integer.parseInt(men1017NumText.getText().toString()));
          }
        }
        else {
          p.setMen1017(0);
        }

        if (men1824Checkbox.isChecked()) {
          if (men1824NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            checkEnteredValueNotLessThanSigninSheetValue(men1824NumText, men1824FromSignInSheet);
            p.setMen1824(Integer.parseInt(men1824NumText.getText().toString()));
          }
        }
        else {
          p.setMen1824(0);
        }

        if (menOver25Checkbox.isChecked()) {
          if (menOver25NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            checkEnteredValueNotLessThanSigninSheetValue(menOver25NumText, menOver25FromSignInSheet);
            p.setMenOver25(Integer.parseInt(menOver25NumText.getText().toString()));
          }
        }
        else {
          p.setMenOver25(0);
        }
        
        if (women09Checkbox.isChecked()) {
          if (women09NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            checkEnteredValueNotLessThanSigninSheetValue(women09NumText, women09FromSignInSheet);
            p.setWomen09(Integer.parseInt(women09NumText.getText().toString()));
          }
        }
        else {
          p.setWomen09(0);
        }

        if (women1017Checkbox.isChecked()) {
          if (women1017NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            checkEnteredValueNotLessThanSigninSheetValue(women1017NumText, women1017FromSignInSheet);
            p.setWomen1017(Integer.parseInt(women1017NumText.getText().toString()));
          }
        }
        else {
          p.setWomen1017(0);
        }

        if (women1824Checkbox.isChecked()) {
          if (women1824NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            checkEnteredValueNotLessThanSigninSheetValue(women1824NumText, women1824FromSignInSheet);
            p.setWomen1824(Integer.parseInt(women1824NumText.getText().toString()));
          }
        }
        else {
          p.setWomen1824(0);
        }

        if (womenOver25Checkbox.isChecked()) {
          if (womenOver25NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            checkEnteredValueNotLessThanSigninSheetValue(womenOver25NumText, womenOver25FromSignInSheet);
            p.setWomenOver25(Integer.parseInt(womenOver25NumText.getText().toString()));
          }
        }
        else {
          p.setWomenOver25(0);
        }
        
        if (spmen09Checkbox.isChecked()) {
          if (spmen09NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            p.setSpMen09(Integer.parseInt(spmen09NumText.getText().toString()));
          }
        }
        else {
          p.setSpMen09(0);
        }
        
        if (spmen1017Checkbox.isChecked()) {
          if (spmen1017NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            p.setSpMen1017(Integer.parseInt(spmen1017NumText.getText().toString()));
          }
        }
        else {
          p.setSpMen1017(0);
        }
        
        if (spmen1824Checkbox.isChecked()) {
          if (spmen1824NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            p.setSpMen1824(Integer.parseInt(spmen1824NumText.getText().toString()));
          }
        }
        else {
          p.setSpMen1824(0);
        }
        
        if (spmenOver25Checkbox.isChecked()) {
          if (spmenOver25NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            p.setSpMenOver25(Integer.parseInt(spmenOver25NumText.getText().toString()));
          }
        }
        else {
          p.setSpMenOver25(0);
        }
        
        if (spwomen09Checkbox.isChecked()) {
          if (spwomen09NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            p.setSpWomen09(Integer.parseInt(spwomen09NumText.getText().toString()));
          }
        }
        else {
          p.setSpWomen09(0);
        }
        
        if (spwomen1017Checkbox.isChecked()) {
          if (spwomen1017NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            p.setSpWomen1017(Integer.parseInt(spwomen1017NumText.getText().toString()));
          }
        }
        else {
          p.setSpWomen1017(0);
        }
        
        if (spwomen1824Checkbox.isChecked()) {
          if (spwomen1824NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            p.setSpWomen1824(Integer.parseInt(spwomen1824NumText.getText().toString()));
          }
        }
        else {
          p.setSpWomen1824(0);
        }
        
        if (spwomenOver25Checkbox.isChecked()) {
          if (spwomenOver25NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else{
            p.setSpWomenOver25(Integer.parseInt(spwomenOver25NumText.getText().toString()));
          }
        }
        else {
          p.setSpWomenOver25(0);
        }
        
        if(errorsFound){
          Toast.makeText(getApplicationContext(), R.string.cannotentersmallernumber,
                  Toast.LENGTH_SHORT).show();
          return;
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

      private void checkEnteredValueNotLessThanSigninSheetValue(EditText editText, int numSignedIn) {
        editText.setTextColor(getResources().getColor(android.R.color.black));
        int enteredValue = Integer.parseInt(editText.getText().toString());
        if(numSignedIn!=0 && enteredValue < numSignedIn){
          editText.setText(Integer.toString(numSignedIn)); // put back at least the number of people signed in
          
          // change the text color to signal an error so that the user can see it easily
          editText.setTextColor(getResources().getColor(R.color.orange));
          
          // restore the text color when the user tries to type in a possible correction
          final EditText fEditText = editText;
          editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
              fEditText.setTextColor(getResources().getColor(android.R.color.black));
              return false;
            }
          });
          errorsFound = true;
        }
      }
    });
  }

  private void updateServiceProviderCounts() {
    if(p.getSpMen09()>0){
      spmen09NumText.setText(Integer.toString(p.getSpMen09()));
      spmen09Checkbox.setChecked(true);
    }
    if(p.getSpMen1017()>0){
      spmen1017NumText.setText(Integer.toString(p.getSpMen1017()));
      spmen1017Checkbox.setChecked(true);
    }
    if(p.getSpMen1824()>0){
      spmen1824NumText.setText(Integer.toString(p.getSpMen1824()));
      spmen1824Checkbox.setChecked(true);
    }
    if(p.getSpMenOver25()>0){
      spmenOver25NumText.setText(Integer.toString(p.getSpMenOver25()));
      spmenOver25Checkbox.setChecked(true);
    }
    if(p.getSpWomen09()>0){
      spwomen09NumText.setText(Integer.toString(p.getSpWomen09()));
      spwomen09Checkbox.setChecked(true);
    }
    if(p.getSpWomen1017()>0){
      spwomen1017NumText.setText(Integer.toString(p.getSpWomen1017()));
      spwomen1017Checkbox.setChecked(true);
    }
    if(p.getSpWomen1824()>0){
      spwomen1824NumText.setText(Integer.toString(p.getSpWomen1824()));
      spwomen1824Checkbox.setChecked(true);
    }
    if(p.getSpWomenOver25()>0){
      spwomenOver25NumText.setText(Integer.toString(p.getSpWomenOver25()));
      spwomenOver25Checkbox.setChecked(true);
    }
  }

  //Handle participant list returned by SignInSheetLandingActivity
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == ADD_PARTICIPANTS_REQUEST) {
      if (resultCode == RESULT_OK) {
        Bundle resultBundle = intent.getExtras();
        participantList = resultBundle.getParcelableArrayList("participantList");
        updateParticipantCounts();
      }
    }
  }

  private void updateParticipantCounts() {
    men09FromSignInSheet = 0;
    men1017FromSignInSheet = 0;
    men1824FromSignInSheet = 0;
    menOver25FromSignInSheet = 0;
    women09FromSignInSheet = 0;
    women1017FromSignInSheet = 0;
    women1824FromSignInSheet = 0;
    womenOver25FromSignInSheet = 0;
    
    for(Participant p: participantList){
      if(p.getGender()==Participant.MALE){
        if(p.getAge()<10)
          men09FromSignInSheet++;
        else if(p.getAge()<18)
          men1017FromSignInSheet++;
        else if(p.getAge()<25)
          men1824FromSignInSheet++;
        else
          menOver25FromSignInSheet++;
      }
      else if(p.getGender()==Participant.FEMALE){ //could simply be an else but just being cautious and making sure the value is FEMALE
        if(p.getAge()<10)
          women09FromSignInSheet++;
        else if(p.getAge()<18)
          women1017FromSignInSheet++;
        else if(p.getAge()<25)
          women1824FromSignInSheet++;
        else
          womenOver25FromSignInSheet++;
      }
    }
    
    if(editParticipation){
      men09ManuallyEntered = p.getMen09() - men09FromSignInSheet;
      men1017ManuallyEntered = p.getMen1017() - men1017FromSignInSheet;
      men1824ManuallyEntered = p.getMen1824() - men1824FromSignInSheet;
      menOver25ManuallyEntered = p.getMenOver25() - menOver25FromSignInSheet;
      women09ManuallyEntered = p.getWomen09() - women09FromSignInSheet;
      women1017ManuallyEntered = p.getWomen1017() - women1017FromSignInSheet;
      women1824ManuallyEntered = p.getWomen1824() - women1824FromSignInSheet;
      womenOver25ManuallyEntered = p.getWomenOver25() - womenOver25FromSignInSheet;
      editParticipation = false; //makes sure we only do the above once
    }
      
    // Note that even though we reinitialize menUnder15, men1524
    // etc to 0 in this method, there is no way their values can be less than what they were the last time around
    // there is no way that a participant once submitted via the sign-in sheet can be removed i.e., these values
    // are strictly increasing.
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalMen09 = men09FromSignInSheet+men09ManuallyEntered;
    if(totalMen09>0){
      men09NumText.setText(Integer.toString(totalMen09));
      men09Checkbox.setChecked(true);
      men09Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalMen1017 = men1017FromSignInSheet+men1017ManuallyEntered;
    if(totalMen1017>0){
      men1017NumText.setText(Integer.toString(totalMen1017));
      men1017Checkbox.setChecked(true);
      men1017Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalMen1824 = men1824FromSignInSheet+men1824ManuallyEntered;
    if(totalMen1824>0){
      men1824NumText.setText(Integer.toString(totalMen1824));
      men1824Checkbox.setChecked(true);
      men1824Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalMenOver25 = menOver25FromSignInSheet+menOver25ManuallyEntered;
    if(totalMenOver25>0){
      menOver25NumText.setText(Integer.toString(totalMenOver25));
      menOver25Checkbox.setChecked(true);
      menOver25Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalWomen09 = women09FromSignInSheet+women09ManuallyEntered;
    if(totalWomen09>0){
      women09NumText.setText(Integer.toString(totalWomen09));
      women09Checkbox.setChecked(true);
      women09Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalWomen1017 = women1017FromSignInSheet+women1017ManuallyEntered;
    if(totalWomen1017>0){
      women1017NumText.setText(Integer.toString(totalWomen1017));
      women1017Checkbox.setChecked(true);
      women1017Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalWomen1824 = women1824FromSignInSheet+women1824ManuallyEntered;
    if(totalWomen1824>0){
      women1824NumText.setText(Integer.toString(totalWomen1824));
      women1824Checkbox.setChecked(true);
      women1824Checkbox.setEnabled(false);
    }
    
    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalWomenOver25 = womenOver25FromSignInSheet+womenOver25ManuallyEntered;
    if(totalWomenOver25>0){
      womenOver25NumText.setText(Integer.toString(totalWomenOver25));
      womenOver25Checkbox.setChecked(true);
      womenOver25Checkbox.setEnabled(false);
    }

    signinSheetButton.setText(getResources().getString(R.string.openSigninSheetButtonLabel)+" ("+participantList.size()+" participant(s))");
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.recordquickparticipationmenu, menu);
    
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
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }
}