package com.hackforchange.views.participationsactive.signinsheet;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.hackforchange.R;
import com.hackforchange.models.activities.Participant;

/*
 * This class provides a landing page for the sign in sheets
 */
public class SignInSheetLandingActivity extends SherlockFragmentActivity {
  private Button okButton;
  private Button doneButton;
  private ArrayList<Participant> participantList;
  private Intent intent;
  
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signinsheetlanding);
    participantList = new ArrayList<Participant>();
    intent = getIntent();
  }

  @Override
  public void onResume() {
    super.onResume();
    
    okButton = (Button) findViewById(R.id.okbutton);
    doneButton = (Button) findViewById(R.id.donebutton);
    
    // go to sign in sheet
    okButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // TODO Auto-generated method stub
        
      }
    });
    
    // go back to RecordQuickParticipationActivity or RecordParticipationActivity
    // send back the number of participants collected so far
    doneButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Bundle resultBundle = new Bundle();
        resultBundle.putParcelableArrayList("participantList", participantList);
        intent.putExtras(resultBundle);
        setResult(RESULT_OK, intent);
        finish();
      }
    });
    
  }

}
