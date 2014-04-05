package com.hackforchange.views.projectsactivities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.projects.ProjectDAO;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.projects.Project;
import com.hackforchange.views.projects.AddProjectActivity;

/*
 * Presents an activity that lists all the projects in the app's database
 * Pressing the back key will exit the activity and take you back to the home screen (WelcomeActivity)
 */
public class AllProjectsActivitiesActivity extends SherlockActivity {
    protected ExpandableListView projectsActivitiesListView; //holds a list of the projects
    List<ProjectsActivitiesHolder> projectsactivities_data, filteredprojectsactivities_data;
    private ProjectsActivitiesListAdapter projectsActivitiesListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allprojectsactivities);
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateProjectsActivitiesList();
    }

    // create actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.allprojectsmenu, menu);

        //used to filter the projects list as the user types or when he submits the query
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
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
        searchView.setOnQueryTextListener(queryTextListener);

        final MenuItem addProject = menu.findItem(R.id.action_addproject);

        // hide the add button when the search view is expanded
        final Menu m = menu;
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m != null) addProject.setVisible(false);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                supportInvalidateOptionsMenu(); // this is needed because Android doesn't remember the Add icon and changes it back
                // to the Settings icon when the Search view is closed
                if (m != null) addProject.setVisible(true);
                return false;
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        return true;
    }

    /**
     * ******************************************************************************************************************
     * transition to view for adding new project when the add icon in the action bar is clicked
     * ******************************************************************************************************************
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // provide a back button on the actionbar
                finish();
                break;
            case R.id.action_addproject:
                Intent intent = new Intent(this, AddProjectActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }


    /**
     * ******************************************************************************************************************
     * check whether the string passed in is present in any of the projects in our list
     * called by queryTextListener
     * ******************************************************************************************************************
     */
    void filterprojectsList(String text) {
        filteredprojectsactivities_data.clear();
        for (int i = 0; i < projectsactivities_data.size(); i++) {
            if (projectsactivities_data.get(i).getProject().getTitle().toLowerCase().matches(".*" + text.toLowerCase() + ".*")) {
                filteredprojectsactivities_data.add(projectsactivities_data.get(i));
            }
        }
        projectsActivitiesListAdapter = new ProjectsActivitiesListAdapter(this, R.layout.row_allprojects,
            R.layout.row_allactivities, projectsActivitiesListView, filteredprojectsactivities_data);
        projectsActivitiesListView.setAdapter(projectsActivitiesListAdapter);
    }

    /**
     * ******************************************************************************************************************
     * populate the projects list
     * list style defined in layout/row_allprojects.xml
     * Source: http://www.ezzylearning.com/tutorial.aspx?tid=1763429
     * ******************************************************************************************************************
     */
    void updateProjectsActivitiesList() {
        ProjectDAO pDao = new ProjectDAO(getApplicationContext());
        ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
        ArrayList<Project> projects_data = pDao.getAllProjects();
        projectsactivities_data = new ArrayList<ProjectsActivitiesHolder>();

        for (Project p : projects_data) {
            ProjectsActivitiesHolder paHolder = new ProjectsActivitiesHolder();
            paHolder.setProject(p);
            List<Activities> activitiesList = aDao.getAllActivitiesForProjectId(p.getId());
            Activities addNewActivityDummy = new Activities();
            addNewActivityDummy.setTitle("Add a new activity...");
            addNewActivityDummy.setId(-1);
            addNewActivityDummy.setProjectid(p.getId());
            activitiesList.add(addNewActivityDummy);
            paHolder.setActivitiesList(activitiesList);
            projectsactivities_data.add(paHolder);
        }
        
        ProjectsActivitiesHolder paHolder = new ProjectsActivitiesHolder();
        Project p = new Project();
        p.setId(-1);
        p.setTitle("Add a new project...");
        paHolder.setProject(p);
        paHolder.setActivitiesList(new ArrayList<Activities>());
        projectsactivities_data.add(paHolder);

        projectsActivitiesListView = (ExpandableListView) findViewById(R.id.projectsactivitieslistView);
        projectsActivitiesListAdapter = new ProjectsActivitiesListAdapter(this, R.layout.row_allprojects, R.layout.row_allactivities, projectsActivitiesListView, projectsactivities_data);
        projectsActivitiesListAdapter.setInflater((getLayoutInflater()));
        projectsActivitiesListView.setAdapter(projectsActivitiesListAdapter);

        // make sure all groups are expanded by default
        for (int i = 0; i < projectsactivities_data.size(); i++) {
            projectsActivitiesListView.expandGroup(i);
        }

        // hide default arrow group indicator because we will provide our own
        projectsActivitiesListView.setGroupIndicator(null);

        filteredprojectsactivities_data = new ArrayList<ProjectsActivitiesHolder>(); //used for filtered data
    }
    
    @Override
    public void onBackPressed() {
      super.onBackPressed();
      overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
      finish();
    }
}