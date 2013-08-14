package com.hackforchange.views.participationsummary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
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
import java.util.Date;

public class ParticipationSummaryActivity extends SherlockActivity {
  private ArrayList<Project> projects_data;
  private StringBuilder s;
  private LinearLayout summaryLayout;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_participationsummary);
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    summaryLayout = (LinearLayout) findViewById(R.id.projectsummarylayout);
    summaryLayout.removeAllViews();
    updateParticipationSummaryList();
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.participationsummarymenu, menu);

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

  private void updateParticipationSummaryList() {
    ProjectDAO projectDAO = new ProjectDAO(getApplicationContext());
    ActivitiesDAO activitiesDAO = new ActivitiesDAO(getApplicationContext());
    ParticipationDAO participationDao = new ParticipationDAO(getApplicationContext());

    projects_data = projectDAO.getAllProjects();
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");

    s = new StringBuilder();
    s.append("Data Report\n");
    s.append("===========\n");

    for (Project p : projects_data) {
      View childProjectView = getLayoutInflater().inflate(R.layout.row_projectsummary, null);
      TextView projectTitle = (TextView) childProjectView.findViewById(R.id.txtTitle);
      projectTitle.setText(p.getTitle());

      s.append("----------------------" + "\n");
      s.append("Project: " + p.getTitle() + "\n");
      s.append("----------------------" + "\n");
      s.append("  Start Date: " + parser.format(p.getStartDate()) + "\n");
      s.append("  End Date: " + parser.format(p.getEndDate()) + "\n");

      ArrayList<Activities> activities_data = activitiesDAO.getAllActivitiesForProjectId(p.getId());

      if(activities_data.size() > 0)
        summaryLayout.addView(childProjectView);

      for (Activities a : activities_data) {
        View childActivityView = getLayoutInflater().inflate(R.layout.row_activitiessummary, null);
        TextView activityTitle = (TextView) childActivityView.findViewById(R.id.txtTitle);
        activityTitle.setText(a.getTitle());

        s.append("    ----------------------" + "\n");
        s.append("    Activity: " + a.getTitle() + "\n");
        s.append("    ----------------------" + "\n");
        s.append("      Start Date: " + parser.format(a.getStartDate()) + "\n");
        s.append("      End Date: " + parser.format(a.getEndDate()) + "\n");

        ArrayList<Participation> participation_data = participationDao.getAllParticipationsForActivityId(a.getId());

        if(participation_data.size() > 0)
          summaryLayout.addView(childActivityView);

        int sumMen = 0, sumWomen = 0;
        for (Participation participation : participation_data) {
          View childParticipationView = getLayoutInflater().inflate(R.layout.row_allparticipation, null);
          TextView participationDate = (TextView) childParticipationView.findViewById(R.id.date);
          Date d = new Date(participation.getDate());
          participationDate.setText(parser.format(d));
          TextView participationMen = (TextView) childParticipationView.findViewById(R.id.men);
          participationMen.setText(participation.getMen()+"");
          TextView participationWomen = (TextView) childParticipationView.findViewById(R.id.women);
          participationWomen.setText(participation.getWomen()+"");
          TextView participationNotes = (TextView) childParticipationView.findViewById(R.id.notes);
          participationNotes.setText("Notes: " + participation.getNotes());
          summaryLayout.addView(childParticipationView);

          sumMen += participation.getMen();
          sumWomen += participation.getWomen();
        }
        s.append("      Total Participation: " + (sumMen + sumWomen) + "\n");
        s.append("        Men: " + sumMen + "\n");
        s.append("        Women: " + sumWomen + "\n");
      }
    }
  }

}