package com.hackforchange.views.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.models.activities.Activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * Presents an activity that displays details of an existing activity
 * Also lets you edit the project (EditActivitiesActivity) or delete the project (right from this java file)
 * by choosing buttons in the ActionBar
 * Pressing the back key will exit the activity
 */
public class DisplayActivitiesActivity extends Activity {
  public String[] AllInits = {"WID", "Youth", "Malaria", "ECPA", "Food Security"};
  private ArrayList<Activities> activities_data, filteredactivities_data;
  private ActivitiesListAdapter listAdapter, tempListAdapter;
  private int id;
  private Activities a;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.displayactivitiesactivity);

    // read in the ID of the project that this activity must display details of
    id = getIntent().getExtras().getInt("projectid");
  }

  @Override
  public void onResume(){
    super.onResume();
    getActionBar().setDisplayHomeAsUpEnabled(true);
    ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
    a = aDao.getActivityWithId(id);
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(a.getTitle());
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    Date d = new Date(a.getStartDate());
    TextView startDate = (TextView) findViewById(R.id.startDate);
    startDate.setText(parser.format(d));
    d = new Date(a.getEndDate());
    TextView endDate = (TextView) findViewById(R.id.endDate);
    endDate.setText(parser.format(d));
    TextView notes = (TextView) findViewById(R.id.notes);
    notes.setText(a.getNotes());
    TextView orgs = (TextView) findViewById(R.id.orgs);
    orgs.setText(a.getOrgs());
    TextView comms = (TextView) findViewById(R.id.comms);
    comms.setText(a.getComms());
    TextView initiatives = (TextView) findViewById(R.id.initiatives);

    // convert initiatives back to human-readable form
    String[] initiativesList = a.getInitiatives().split("|");
    String inits = "";
    for(int i=0; i<initiativesList.length; i++){
      if(initiativesList[i]=="1")
        inits += AllInits[i]+"\n";
    }
    initiatives.setText(inits);
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.displayactivitiesmenu, menu);
    getActionBar().setDisplayShowTitleEnabled(true);
    return true;
  }

  /*********************************************************************************************************************
   * transition to view for adding new project when the add icon in the action bar is clicked
   ********************************************************************************************************************/
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case android.R.id.home:
        // provide a back button on the actionbar
        finish();
        break;
      case R.id.action_deleteproject:
        // warn the user first!
        new AlertDialog.Builder(this)
          .setMessage("Are you sure you want to delete this project? This CANNOT be undone.")
          .setCancelable(false)
          .setNegativeButton("No", null)
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              ActivitiesDAO pDao = new ActivitiesDAO(getApplicationContext());
              pDao.deleteActivities(DisplayActivitiesActivity.this.id);
              finish();
            }
          })
          .show();
        break;
      case R.id.action_editproject:
        //Intent i = new Intent(DisplayActivitiesActivity.this, EditActivitiesActivity.class);
        //i.putExtra("projectid",id);
        //startActivity(i);
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }
}