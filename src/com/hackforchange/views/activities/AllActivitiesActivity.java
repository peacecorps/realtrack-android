package com.hackforchange.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.models.activities.Activities;

import java.util.ArrayList;

/*
 * Presents an activity that lists all the activities associated with a activities
 */
public class AllActivitiesActivity extends Activity {
  private ListView activitieslist; //holds a list of the activities
  private ArrayList<Activities> activities_data, filteredactivities_data;
  private ActivitiesListAdapter listAdapter, tempListAdapter;
  private int projectid;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.allactivitiesactivity);

    // get the owner activities
    projectid = getIntent().getExtras().getInt("projectid");
  }

  @Override
  public void onResume(){
    super.onResume();
    getActionBar().setDisplayHomeAsUpEnabled(true);
    ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
    activities_data = aDao.getAllActivitiesForProjectId(projectid);
    filteredactivities_data = new ArrayList<Activities>(); //used for filtered data
    updateActivitiesList();
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.allactivitiesmenu, menu);

    //used to filter the activities list as the user types or when he submits the query
    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener(){
      @Override
      public boolean onQueryTextSubmit(String query) {
        filteractivitiesList(query);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        filteractivitiesList(newText);
        return false;
      }
    };

    // set the text listener for the Search field
    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    searchView.setOnQueryTextListener (queryTextListener);

    final MenuItem addActivities = menu.findItem(R.id.action_addactivities);

    // hide the add button when the search view is expanded
    final Menu m = menu;
    searchView.setOnSearchClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (m!=null) addActivities.setVisible(false);
      }
    });
    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override
      public boolean onClose() {
        invalidateOptionsMenu(); // this is needed because Android doesn't remember the Add icon and changes it back
        // to the Settings icon when the Search view is closed
        if (m!=null) addActivities.setVisible(true);
        return false;
      }
    });
    getActionBar().setDisplayShowTitleEnabled(true);
    return true;
  }

  /*********************************************************************************************************************
   * transition to view for adding new activities when the add icon in the action bar is clicked
   ********************************************************************************************************************/
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case android.R.id.home:
        // provide a back button on the actionbar
        finish();
        break;
      case R.id.action_addactivities:
        Intent i = new Intent(this, AddActivitiesActivity.class);
        i.putExtra("projectid",this.projectid);
        this.startActivity(i);
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }


  /*********************************************************************************************************************
   * check whether the string passed in is present in any of the activities in our list
   * called by queryTextListener
   ********************************************************************************************************************/
  void filteractivitiesList(String text){
    filteredactivities_data.clear();
    for(int i=0;i<activities_data.size();i++){
      if(activities_data.get(i).getTitle().toLowerCase().matches(".*" + text.toLowerCase() + ".*")){
        filteredactivities_data.add(activities_data.get(i));
      }
    }
    listAdapter = new ActivitiesListAdapter(AllActivitiesActivity.this, R.layout.activitieslist_row, filteredactivities_data);
    activitieslist.setAdapter(listAdapter);
  }

  /*********************************************************************************************************************
   * populate the activities list
   * list style defined in layout/activitieslist_row.xml
   * Source: http://www.ezzylearning.com/tutorial.aspx?tid=1763429
   ********************************************************************************************************************/
  void updateActivitiesList(){
    listAdapter = new ActivitiesListAdapter(this, R.layout.activitieslist_row, activities_data);
    activitieslist = (ListView)findViewById(R.id.activitieslistView);
    activitieslist.setAdapter(listAdapter);
    activitieslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Activities a = (Activities) listAdapter.getItem(position);
        Intent i = new Intent(AllActivitiesActivity.this, DisplayActivitiesActivity.class);
        i.putExtra("activitiesid", a.getId());
        startActivity(i);
      }
    });
  }
}
