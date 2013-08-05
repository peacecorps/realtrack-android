package com.hackforchange.views.participationsummary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.backend.projects.ProjectDAO;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.models.projects.Project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ParticipationSummaryActivity extends SherlockActivity {
  private ListView projectSummaryListView;
  private List<ProjectHolder> projectHolderList, filteredProjectHolderList;
  private ArrayList<Project> projects_data;
  private ProjectSummaryListAdapter projectSummaryListAdapter;
  private StringBuilder s;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_participationsummary);
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    updateParticipationSummaryList();
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.participationsummarymenu, menu);

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

    final MenuItem exportDataMenuItem = menu.findItem(R.id.action_exportdata);

    // hide the add button when the search view is expanded
    final Menu m = menu;
    searchView.setOnSearchClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (m != null) exportDataMenuItem.setVisible(false);
      }
    });
    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override
      public boolean onClose() {
        invalidateOptionsMenu(); // this is needed because Android doesn't remember the Add icon and changes it back
        // to the Settings icon when the Search view is closed
        if (m != null) exportDataMenuItem.setVisible(true);
        return false;
      }
    });

    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // provide a back button on the actionbar
        finish();
        break;
      case R.id.action_exportdata:
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("") +
          "?subject=" + Uri.encode("RealTrack Data Report") +
          "&body=" + Uri.encode(s.toString());
        Uri uri = Uri.parse(uriText);
        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send mail..."));
        break;
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
    filteredProjectHolderList.clear();
    for (int i = 0; i < projectHolderList.size(); i++) {
      if (projectHolderList.get(i).getTitle().toLowerCase().matches(".*" + text.toLowerCase() + ".*")) {
        filteredProjectHolderList.add(projectHolderList.get(i));
      }
    }
    projectSummaryListAdapter = new ProjectSummaryListAdapter(this, R.layout.row_projectsummary, filteredProjectHolderList);
    projectSummaryListView.setAdapter(projectSummaryListAdapter);
  }

  private void updateParticipationSummaryList() {
    projectSummaryListView = (ListView) findViewById(R.id.projectsummarylistview);
    projectHolderList = new ArrayList<ProjectHolder>();
    ProjectDAO projectDAO = new ProjectDAO(getApplicationContext());
    ActivitiesDAO activitiesDAO = new ActivitiesDAO(getApplicationContext());
    ParticipationDAO participationDao = new ParticipationDAO(getApplicationContext());
    projects_data = projectDAO.getAllProjects();
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    s = new StringBuilder();
    s.append("Data Report\n");
    s.append("===========\n");
    for (Project p : projects_data) {
      ProjectHolder pHolder = new ProjectHolder();
      pHolder.setTitle(p.getTitle());
      List<ActivitiesHolder> activitiesHolderList = new ArrayList<ActivitiesHolder>();
      s.append("----------------------" + "\n");
      s.append("Project: " + p.getTitle() + "\n");
      s.append("----------------------" + "\n");
      s.append("  Start Date: " + parser.format(p.getStartDate()) + "\n");
      s.append("  End Date: " + parser.format(p.getEndDate()) + "\n");
      ArrayList<Activities> activities_data = activitiesDAO.getAllActivitiesForProjectId(p.getId());
      for (Activities a : activities_data) {
        s.append("    ----------------------" + "\n");
        s.append("    Activity: " + a.getTitle() + "\n");
        s.append("    ----------------------" + "\n");
        s.append("      Start Date: " + parser.format(a.getStartDate()) + "\n");
        s.append("      End Date: " + parser.format(a.getEndDate()) + "\n");
        ActivitiesHolder aHolder = new ActivitiesHolder();
        aHolder.setTitle(a.getTitle());
        ArrayList<Participation> participation_data = participationDao.getAllParticipationsForActivityId(a.getId());
        if (participation_data.size() > 0) {
          aHolder.setParticipationList(participation_data);
          activitiesHolderList.add(aHolder);
        }
        int sumMen = 0, sumWomen = 0;
        for (Participation participation : participation_data) {
          sumMen += participation.getMen();
          sumWomen += participation.getWomen();
        }
        s.append("      Total Participation: " + (sumMen + sumWomen) + "\n");
        s.append("        Men: " + sumMen + "\n");
        s.append("        Women: " + sumWomen + "\n");
      }
      if (activitiesHolderList.size() > 0) {
        pHolder.setActivitiesHolderList(activitiesHolderList);
        projectHolderList.add(pHolder);
      }
    }

    projectSummaryListAdapter = new ProjectSummaryListAdapter(this, R.layout.row_projectsummary, projectHolderList);
    projectSummaryListView.setAdapter(projectSummaryListAdapter);
    filteredProjectHolderList = new ArrayList<ProjectHolder>(); //used for filtered data
  }
}