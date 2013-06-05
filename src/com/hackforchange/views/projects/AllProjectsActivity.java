package com.hackforchange.views.projects;

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
import com.hackforchange.models.projects.Project;
import com.hackforchange.backend.projects.ProjectDAO;

import java.util.ArrayList;

/*
 * Presents an activity that lists all the projects in the app's database
 * Pressing the back key will exit the activity and take you back to the home screen (WelcomeActivity)
 */
public class AllProjectsActivity extends Activity {
  private ListView projectslist; //holds a list of the projects
  private ArrayList<Project> projects_data, filteredprojects_data;
  private ProjectListAdapter listAdapter, tempListAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.mainactivity);
  }

  @Override
  public void onResume(){
    super.onResume();
    getActionBar().setDisplayHomeAsUpEnabled(true);
    ProjectDAO pDao = new ProjectDAO(getApplicationContext());
    projects_data = pDao.getAllProjects();
    filteredprojects_data = new ArrayList<Project>(); //used for filtered data
    updateProjectsList();
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.allprojectsmenu, menu);

    //used to filter the projects list as the user types or when he submits the query
    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener(){
      @Override
      public boolean onQueryTextSubmit(String query) {
        filterprojectsList(query);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        filterprojectsList(newText);
        return false;
      }
    };

    // set the text listener for the Search field
    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    searchView.setOnQueryTextListener (queryTextListener);

    final MenuItem addProject = menu.findItem(R.id.action_addproject);

    // hide the add button when the search view is expanded
    final Menu m = menu;
    searchView.setOnSearchClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (m!=null) addProject.setVisible(false);
      }
    });
    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override
      public boolean onClose() {
        invalidateOptionsMenu(); // this is needed because Android doesn't remember the Add icon and changes it back
                                 // to the Settings icon when the Search view is closed
        if (m!=null) addProject.setVisible(true);
        return false;
      }
    });

    getActionBar().setDisplayShowHomeEnabled(true);
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
      case R.id.action_addproject:
        Intent intent = new Intent(this, AddProjectActivity.class);
        this.startActivity(intent);
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }


  /*********************************************************************************************************************
   * check whether the string passed in is present in any of the projects in our list
   * called by queryTextListener
   ********************************************************************************************************************/
  void filterprojectsList(String text){
    filteredprojects_data.clear();
    for(int i=0;i<projects_data.size();i++){
      if(projects_data.get(i).getTitle().toLowerCase().matches(".*" + text.toLowerCase() + ".*")){
        filteredprojects_data.add(projects_data.get(i));
      }
    }
    listAdapter = new ProjectListAdapter(AllProjectsActivity.this, R.layout.projectslist_row, filteredprojects_data);
    projectslist.setAdapter(listAdapter);
  }

  /*********************************************************************************************************************
   * populate the projects list
   * list style defined in layout/projectslist_row.xml
   * Source: http://www.ezzylearning.com/tutorial.aspx?tid=1763429
   ********************************************************************************************************************/
  void updateProjectsList(){
    listAdapter = new ProjectListAdapter(this, R.layout.projectslist_row, projects_data);
    projectslist = (ListView)findViewById(R.id.homeitemlistView);
    projectslist.setAdapter(listAdapter);
    projectslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Project p = (Project) listAdapter.getItem(position);
        Intent i = new Intent(AllProjectsActivity.this, DisplayProjectActivity.class);
        i.putExtra("projectid", p.getId());
        startActivity(i);
      }
    });
  }
}
