package com.hackforchange.views.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import com.hackforchange.R;
import com.hackforchange.views.projects.AddProjectActivity;
import com.hackforchange.views.projects.AllProjectsActivity;

import java.util.ArrayList;

// TODO:
// 1. Add back button on Actionbar for editing activity, adding activity
// 2. Add cancel button for editing activity, adding activity
public class WelcomeActivity extends Activity {
  private ListView homeitemslist; //holds a list of the homeitems
  private ArrayList<String> homeitems_data, filteredhomeitems_data;
  private HomeItemListAdapter listAdapter, tempListAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.mainactivity);

    // Items on welcome screen
    // 1. Pending
    // 2. All Projects
    // 3. New Project
    // 4. Export
    homeitems_data = new ArrayList<String>();
    homeitems_data.add("Pending");
    homeitems_data.add("All Projects");
    homeitems_data.add("New Project");
    homeitems_data.add("Export");
    filteredhomeitems_data = new ArrayList<String>(); //used for filtered data
    updateHomeItemsList();
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);

    //used to filter the homeitems list as the user types or when he submits the query
    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener(){
      @Override
      public boolean onQueryTextSubmit(String query) {
        filterhomeitemsList(query);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        filterhomeitemsList(newText);
        return false;
      }
    };

    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    //searchView.setIconified(false); //expand the search field by default
    searchView.setOnQueryTextListener (queryTextListener);
    getActionBar().setDisplayShowTitleEnabled(true);
    return true;
  }

  /*********************************************************************************************************************
   * check whether the string passed in is present in any of the homeitems in our list
   * called by queryTextListener
   ********************************************************************************************************************/
  void filterhomeitemsList(String text){
    filteredhomeitems_data.clear();
    for(int i=0;i<homeitems_data.size();i++){
      if(homeitems_data.get(i).toLowerCase().matches(".*"+text.toLowerCase()+".*")){
        filteredhomeitems_data.add(homeitems_data.get(i));
      }
    }
    listAdapter = new HomeItemListAdapter(WelcomeActivity.this, R.layout.homeitemslist_row, filteredhomeitems_data);
    homeitemslist.setAdapter(listAdapter);
  }

  /*********************************************************************************************************************
   * populate the homeitems list
   * list style defined in layout/weaponslist_row.xml
   * Source: http://www.ezzylearning.com/tutorial.aspx?tid=1763429
   ********************************************************************************************************************/
  void updateHomeItemsList(){
    listAdapter = new HomeItemListAdapter(this, R.layout.homeitemslist_row, homeitems_data);
    homeitemslist = (ListView)findViewById(R.id.homeitemlistView);
    homeitemslist.setAdapter(listAdapter);
    homeitemslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
          case 0: // PENDING
            break;
          case 1: // ALL PROJECTS
            Intent newActivity = new Intent(WelcomeActivity.this, AllProjectsActivity.class);
            startActivity(newActivity);
            break;
          case 2: // NEW PROJECT
            newActivity = new Intent(WelcomeActivity.this, AddProjectActivity.class);
            startActivity(newActivity);
            break;
          case 3: // EXPORT
            break;
        }
      }
    });
  }
}
