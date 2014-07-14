package com.realtrackandroid.views.welcome;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ParticipationDAO;
import com.realtrackandroid.common.StyledButton;
import com.realtrackandroid.models.activities.Participation;
import com.realtrackandroid.views.help.HelpDialog;
import com.realtrackandroid.views.participationsdonesummaries.ParticipationSummaryActivity;
import com.realtrackandroid.views.participationspending.PendingParticipationActivity;
import com.realtrackandroid.views.projectsactivities.AllProjectsActivitiesActivity;

/**
 * This is the home screen of the app.
 * 
 * @author Raj
 */
public class WelcomeActivity extends SherlockFragmentActivity {
  private ArrayList<Participation> unservicedParticipation_data;

  private StyledButton myProjectsBtn, myDataBtn, pendingParticipationsBtn;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
  }

  @Override
  public void onResume() {
    super.onResume();

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    if (!prefs.contains(getString(R.string.name))) {
      Intent i = new Intent(this, CollectPCVInfoActivity.class);
      this.startActivity(i);
      this.finish();
    }
    else {
      TextView greetingTextView = (TextView) findViewById(R.id.greetingTextView);
      if (prefs.contains(getString(R.string.name)))
        greetingTextView.setText("Hello, " + prefs.getString(getString(R.string.name), ""));

      myProjectsBtn = (StyledButton) findViewById(R.id.myprojectsbutton);
      myProjectsBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent newActivity = new Intent(WelcomeActivity.this, AllProjectsActivitiesActivity.class);
          startActivity(newActivity);
          overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      myDataBtn = (StyledButton) findViewById(R.id.mydatabutton);
      myDataBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent newActivity = new Intent(WelcomeActivity.this, ParticipationSummaryActivity.class);
          startActivity(newActivity);
          overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      pendingParticipationsBtn = (StyledButton) findViewById(R.id.pendingParticipationsBtn);
      pendingParticipationsBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent newActivity = new Intent(WelcomeActivity.this, PendingParticipationActivity.class);
          startActivity(newActivity);
          overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());
      unservicedParticipation_data = pDao.getAllUnservicedParticipations();
      if (unservicedParticipation_data.size() != 0)
        pendingParticipationsBtn.setText(getResources().getString(R.string.fa_calendar)
                + " Pending (" + unservicedParticipation_data.size() + ")");
      else
        pendingParticipationsBtn.setVisibility(View.GONE);

    }
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.welcomeactivitymenu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_help:
        HelpDialog helpDialog = new HelpDialog();
        helpDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        helpDialog.show(getSupportFragmentManager(), "helpdialog");
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }
}