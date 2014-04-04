package com.hackforchange.views.participationsactive.signinsheet;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.hackforchange.R;
import com.hackforchange.models.activities.Participant;

/*
 * This class provides a landing page for the sign in sheets
 */
public class SignInSheetLandingActivity extends SherlockFragmentActivity {
  static final int SIGNIN_REQUEST = 1;
  
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
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
    okButton = (Button) findViewById(R.id.okbutton);
    doneButton = (Button) findViewById(R.id.donebutton);
    
    // go to sign in sheet
    okButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(getApplicationContext(), SignInSheetActivity.class);
        i.putExtra("activitytitle", intent.getExtras().getString("activitytitle"));
        if(intent.hasExtra("participationdate")){
          i.putExtra("participationdate", intent.getExtras().getString("participationdate"));
        }
        startActivityForResult(i, SIGNIN_REQUEST);
        overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
      }
    });
    
    // go back to RecordQuickParticipationActivity or RecordParticipationActivity
    // send back the participants collected so far
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
    
    if(intent.hasExtra("firstOpen")){
      intent.removeExtra("firstOpen");
      okButton.performClick();
    }
    
  }
  
  // Handle participant returned by SignInSheetActivity
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == SIGNIN_REQUEST) {
      if (resultCode == RESULT_OK) {
        Bundle resultBundle = intent.getExtras();
        Participant currentParticipant = resultBundle.getParcelable("participant");
        
        if(currentParticipant!=null)
          participantList.add(currentParticipant);
      }
    }
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
