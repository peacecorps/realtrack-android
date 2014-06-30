package com.realtrackandroid.views.participationspending;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ParticipationDAO;
import com.realtrackandroid.models.activities.Participation;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.HelpDialog;
import com.realtrackandroid.views.participationsactive.RecordOrEditParticipationActivity;

public class PendingParticipationActivity extends SherlockFragmentActivity {
  private ArrayList<Participation> unservicedParticipation_data;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    setContentView(R.layout.activity_pendingparticipation);
  }

  @Override
  public void onResume() {
    super.onResume();
    ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());
    unservicedParticipation_data = pDao.getAllUnservicedParticipations();

    PendingParticipationListAdapter listAdapter = new PendingParticipationListAdapter(this, R.layout.row_pendingparticipation, unservicedParticipation_data);
    ListView participationitemslist = (ListView) findViewById(R.id.pendingparticipationlistView);
    participationitemslist.setAdapter(listAdapter);
    participationitemslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Participation p = unservicedParticipation_data.get(position);
        // if the user is already on the pending participations screen when a notification pops up or comes to it
        // from the home screen's "Pending" button (and not by clicking the notifification), clear the corresponding
        // notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(p.getId());
        // clicking on the item must take the user to the record participation activity
        Intent newActivity = new Intent(PendingParticipationActivity.this, RecordOrEditParticipationActivity.class);
        newActivity.putExtra("participationid", p.getId());
        newActivity.putExtra("datetime", p.getDate());
        startActivity(newActivity);
        overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.pendingparticipationmenu, menu);

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