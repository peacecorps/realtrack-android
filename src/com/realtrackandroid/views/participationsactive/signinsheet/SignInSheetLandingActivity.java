package com.realtrackandroid.views.participationsactive.signinsheet;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.common.StyledButton;
import com.realtrackandroid.models.activities.Participant;

/*
 * This class provides a landing page for the sign in sheets
 */
public class SignInSheetLandingActivity extends SherlockFragmentActivity {
  static final int SIGNIN_REQUEST = 1;

  private StyledButton okButton;

  private StyledButton doneButton;

  private StyledButton reviewButton;

  private ArrayList<Participant> participantList;

  private Intent intent;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signinsheetlanding);
    intent = getIntent();
    Bundle resultBundle = intent.getExtras();
    participantList = resultBundle.getParcelableArrayList("participantList");
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    okButton = (StyledButton) findViewById(R.id.okbutton);
    doneButton = (StyledButton) findViewById(R.id.doneButton);
    reviewButton = (StyledButton) findViewById(R.id.reviewButton);

    if (participantList.isEmpty()) {
      reviewButton.setVisibility(View.INVISIBLE);
      ((TextView) findViewById(R.id.reviewMsg)).setVisibility(View.INVISIBLE);
    }
    else {
      reviewButton.setVisibility(View.VISIBLE);
      ((TextView) findViewById(R.id.reviewMsg)).setVisibility(View.VISIBLE);
    }

    // go to sign in sheet
    okButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(getApplicationContext(), SignInSheetActivity.class);
        i.putExtra("activitytitle", intent.getExtras().getString("activitytitle"));
        if (intent.hasExtra("participationdate")) {
          i.putExtra("participationdate", intent.getExtras().getString("participationdate"));
        }
        startActivityForResult(i, SIGNIN_REQUEST);
        overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
      }
    });

    // go back to RecordQuickParticipationActivity or RecordOrEditParticipationActivity
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

    if (intent.hasExtra("firstOpen")) {
      intent.removeExtra("firstOpen");
      okButton.performClick();
    }

    // go back to RecordQuickParticipationActivity or RecordOrEditParticipationActivity
    // send back the participants collected so far
    reviewButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(getApplicationContext(), ReviewSignInActivity.class);
        Bundle resultBundle = new Bundle();
        resultBundle.putParcelableArrayList("participantList", participantList);
        i.putExtras(resultBundle);
        startActivity(i);
      }
    });

  }

  // Handle participant returned by SignInSheetActivity
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == SIGNIN_REQUEST) {
      if (resultCode == RESULT_OK) {
        Bundle resultBundle = intent.getExtras();
        Participant currentParticipant = resultBundle.getParcelable("participant");

        if (currentParticipant != null)
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

  @Override
  protected void onSaveInstanceState(Bundle out) {
    super.onSaveInstanceState(out);
    out.putParcelableArrayList("participantList", participantList);
  }

  @Override
  protected void onRestoreInstanceState(Bundle in) {
    super.onRestoreInstanceState(in);
    participantList = in.getParcelableArrayList("participantList");
  }

}
