package com.hackforchange.views.welcome;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.views.help.HelpDialog;

public class WelcomeActivity extends SherlockFragmentActivity {
  private ListView homeitemslist; //holds a list of homeitems
  private ArrayList<String> homeitems_data;
  private HomeItemListAdapter listAdapter;
  private ArrayList<Participation> unservicedParticipation_data;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcomeactivity);
  }

  @Override
  public void onResume() {
    super.onResume();

    // Items on welcome screen
    // 1. Pending
    // 2. All Projects
    // 3. New Project
    // 4. Export
    homeitems_data = new ArrayList<String>();
    homeitems_data.add(getResources().getString(R.string.fa_list)+" My Projects");
    homeitems_data.add(getResources().getString(R.string.fa_table)+" My Data");
    ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());
    unservicedParticipation_data = pDao.getAllUnservicedParticipations();
    if (unservicedParticipation_data.size() != 0) {
      homeitems_data.add(getResources().getString(R.string.fa_calendar)+" Pending (" + unservicedParticipation_data.size() + ")");
    }

    // populate the home items list
    updateHomeItemsList();
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


  /**
   * ******************************************************************************************************************
   * populate the homeitems list
   * list style defined in layout/weaponslist_row.xml
   * Source: http://www.ezzylearning.com/tutorial.aspx?tid=1763429
   * ******************************************************************************************************************
   */
  void updateHomeItemsList() {
    listAdapter = new HomeItemListAdapter(this, R.layout.row_homeitems, homeitems_data);
    homeitemslist = (ListView) findViewById(R.id.homeitemlistView);
    homeitemslist.setAdapter(listAdapter);
  }
}