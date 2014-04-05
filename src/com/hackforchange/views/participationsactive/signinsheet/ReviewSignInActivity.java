package com.hackforchange.views.participationsactive.signinsheet;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.hackforchange.R;
import com.hackforchange.common.StyledButton;
import com.hackforchange.models.activities.Participant;

public class ReviewSignInActivity extends SherlockFragmentActivity {
  private ArrayList<Participant> participantList;
  private StyledButton doneButton;
  private Intent intent;
  private TextView headCount;
  
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reviewsignin);
    participantList = new ArrayList<Participant>();
    intent = getIntent();
    
    Bundle resultBundle = intent.getExtras();
    List<Participant> currentParticipantList = resultBundle.getParcelableArrayList("participantList");
    participantList.addAll(currentParticipantList);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
    doneButton = (StyledButton) findViewById(R.id.doneButton);
    doneButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
        finish(); 
      }
    });
    
    headCount = (TextView) findViewById(R.id.headcount);
    headCount.setText("Current Head Count: "+participantList.size());
    
    ReviewSigninSheetListAdapter listAdapter = new ReviewSigninSheetListAdapter(this, R.layout.row_reviewsigninparticipants, participantList);
    ListView participationitemslist = (ListView) findViewById(R.id.participantlistView);
    participationitemslist.setAdapter(listAdapter);
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
