package com.hackforchange.views.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.backend.reminders.RemindersDAO;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.models.reminders.Reminders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/*
 * Presents an activity that lists all the participation associated with a participation
 */
public class AllParticipationActivity extends Activity {
  private ListView participationlist; //holds a list of the participation
  private ArrayList<Participation> participation_data, filteredparticipation_data;
  private AllParticipationsListAdapter listAdapter;
  private int activitiesid;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.allparticipationactivity);

    // get the owner participation
    activitiesid = getIntent().getExtras().getInt("activitiesid");
  }

  @Override
  public void onResume() {
    super.onResume();
    getActionBar().setDisplayHomeAsUpEnabled(true);
    updateParticipationList();
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.allparticipationmenu, menu);

    //used to filter the participation list as the user types or when he submits the query
    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        filterparticipationList(query);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        filterparticipationList(newText);
        return false;
      }
    };

    // set the text listener for the Search field
    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    searchView.setOnQueryTextListener(queryTextListener);
    getActionBar().setDisplayShowTitleEnabled(true);
    return true;
  }

  /**
   * ******************************************************************************************************************
   * transition to view for adding new participation when the add icon in the action bar is clicked
   * ******************************************************************************************************************
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // provide a back button on the actionbar
        finish();
        break;
      case R.id.action_deleteactivity:
        // warn the user first!
        new AlertDialog.Builder(this)
          .setMessage("Are you sure you want to delete all participation records? This CANNOT be undone.")
          .setCancelable(false)
          .setNegativeButton("No", null)
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());
              for (Participation p : participation_data) {
                pDao.deleteParticipation(p.getId());
              }
              finish();
            }
          })
          .show();
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }


  /**
   * ******************************************************************************************************************
   * check whether the string passed in is present in any of the participation in our list
   * called by queryTextListener
   * ******************************************************************************************************************
   */
  void filterparticipationList(String text) {
    filteredparticipation_data.clear();
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy, hh:mm aaa"); // example: 07/04/2013, 6:13 PM
    for (Participation p : participation_data) {
      if (parser.format(p.getDate()).toString().toLowerCase().matches(".*" + text.toLowerCase() + ".*")) {
        filteredparticipation_data.add(p);
      }
    }

    listAdapter = new AllParticipationsListAdapter(AllParticipationActivity.this, R.layout.allparticipationlist_row, filteredparticipation_data);
    participationlist.setAdapter(listAdapter);
  }

  /**
   * ******************************************************************************************************************
   * populate the participation list
   * list style defined in layout/participationlist_row.xml
   * Source: http://www.ezzylearning.com/tutorial.aspx?tid=1763429
   * ******************************************************************************************************************
   */
  void updateParticipationList() {
    // fetch all the participation records for this activity
    RemindersDAO rDao = new RemindersDAO(getApplicationContext());
    participation_data = new ArrayList<Participation>();
    ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());
    participation_data = pDao.getAllParticipationsForActivityId(activitiesid);

    listAdapter = new AllParticipationsListAdapter(this, R.layout.allparticipationlist_row, this.participation_data);
    participationlist = (ListView) findViewById(R.id.participationdetailslistview);
    participationlist.setAdapter(listAdapter);

    filteredparticipation_data = new ArrayList<Participation>(); //used for filtered data
  }
}
