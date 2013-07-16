package com.hackforchange.views.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.views.activities.PendingParticipationActivity;
import com.hackforchange.views.projects.AddProjectActivity;
import com.hackforchange.views.projects.AllProjectsActivity;

import java.util.ArrayList;

// TODO: Add cancel button for editing activity, adding activity.
public class WelcomeActivity extends Activity {
  private ListView homeitemslist; //holds a list of the homeitems
  private ArrayList<String> homeitems_data, filteredhomeitems_data;
  private HomeItemListAdapter listAdapter;
  private ArrayList<Participation> unservicedParticipation_data;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.mainactivity);
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
    ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());
    unservicedParticipation_data = pDao.getAllUnservicedParticipations();
    if (unservicedParticipation_data.size() != 0)
      homeitems_data.add("Pending (" + unservicedParticipation_data.size() + ")");
    homeitems_data.add("All Projects");
    homeitems_data.add("New Project");
    homeitems_data.add("Export");
    filteredhomeitems_data = new ArrayList<String>(); //used for filtered data

    // populate the home items list
    updateHomeItemsList();
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
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
    listAdapter = new HomeItemListAdapter(this, R.layout.homeitemslist_row, homeitems_data);
    homeitemslist = (ListView) findViewById(R.id.homeitemlistView);
    homeitemslist.setAdapter(listAdapter);
    homeitemslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (unservicedParticipation_data.size() != 0) {
          switch (position) {
            case 0: // PENDING
              Intent newActivity = new Intent(WelcomeActivity.this, PendingParticipationActivity.class);
              startActivity(newActivity);
              break;
            case 1: // ALL PROJECTS
              newActivity = new Intent(WelcomeActivity.this, AllProjectsActivity.class);
              startActivity(newActivity);
              break;
            case 2: // NEW PROJECT
              newActivity = new Intent(WelcomeActivity.this, AddProjectActivity.class);
              startActivity(newActivity);
              break;
            case 3: // EXPORT
              // TODO: process men and women count
              // TODO: generate report in xls format
              // TODO: attach xls to email?
              break;
          }
        }
        else{ // no pending participations, hencing the 'Pending' button is not shown
          switch (position) {
            case 0: // ALL PROJECTS
              Intent newActivity = new Intent(WelcomeActivity.this, AllProjectsActivity.class);
              startActivity(newActivity);
              break;
            case 1: // NEW PROJECT
              newActivity = new Intent(WelcomeActivity.this, AddProjectActivity.class);
              startActivity(newActivity);
              break;
            case 2: // EXPORT
              // TODO: process men and women count
              // TODO: generate report in xls format
              // TODO: attach xls to email?
              break;
          }
        }
      }
    });
  }
}