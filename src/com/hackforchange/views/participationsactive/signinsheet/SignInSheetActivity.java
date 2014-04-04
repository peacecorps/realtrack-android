package com.hackforchange.views.participationsactive.signinsheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.hackforchange.R;
import com.hackforchange.models.activities.Participant;

public class SignInSheetActivity extends SherlockActivity {
  private Button submitButton;
  private Intent intent; 
  private EditText nameText, phoneText, villageText, ageText;
  private TextView signInMessage;
  private RadioButton maleRadioButton;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signinsheet);
    intent = getIntent();
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
    String signInMsg = intent.getExtras().getString("activitytitle");
    
    if(intent.hasExtra("participationdate")){ //we need to check because there's a chance this is not present (from RQPA if user does not enter date)
      signInMsg = signInMsg + " " + intent.getExtras().getString("participationdate");
    }
    
    signInMessage = (TextView) findViewById(R.id.pleasesignin);
    signInMessage.setText(signInMsg);
    
    nameText = (EditText) findViewById(R.id.nameEditText);
    phoneText = (EditText) findViewById(R.id.phoneEditText);
    villageText = (EditText) findViewById(R.id.villageEditText);
    ageText = (EditText) findViewById(R.id.ageEditText);
    submitButton = (Button) findViewById(R.id.submitbutton);
    maleRadioButton = (RadioButton) findViewById(R.id.maleRadioButton);
    
    // go back to SignInSheetLandingActivity
    // send back the participant just entered
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Participant p = new Participant();
        
        if (nameText.getText().length() == 0){
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage,
                  Toast.LENGTH_SHORT).show();
          return;
        }
        else
          p.setName(nameText.getText().toString());
        
        p.setPhoneNumber(phoneText.getText().toString());
        
        p.setVillage(villageText.getText().toString());
        
        if (ageText.getText().length() == 0){
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage,
                  Toast.LENGTH_SHORT).show();
          return;
        }
        else
          p.setAge(Integer.parseInt(ageText.getText().toString()));
        
        if(maleRadioButton.isChecked())
          p.setGender(Participant.MALE);
        else
          p.setGender(Participant.FEMALE);
        
        Bundle resultBundle = new Bundle();
        resultBundle.putParcelable("participant", p);
        intent.putExtras(resultBundle);
        setResult(RESULT_OK, intent);
        finish();
      }
    });
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
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
