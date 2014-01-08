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
import com.hackforchange.providers.CachedFileContentProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ParticipationSummaryActivity extends SherlockActivity {
    public static final String[] AllInits = {"WID", "Youth", "Malaria", "ECPA", "Food Security"};
    private ArrayList<Project> projects_data;
    private StringBuilder emailContent;
    private LinearLayout summaryLayout;
    File cacheDir, cacheOutputFile;
    String fileName;

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
        DateFormat dateParser = new SimpleDateFormat("MMddyyyy");
        fileName = "RealTrack_Data_Report_" + dateParser.format(Calendar.getInstance().getTimeInMillis()) + ".csv";
        cacheDir = getApplicationContext().getCacheDir(); // context being the Activity pointer
        cacheOutputFile = new File(cacheDir + File.separator + fileName);
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
                Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO);
                sendEmailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendEmailIntent.setType("plain/text");
                String uriText = "mailto:" + Uri.encode("") +
                    "?subject=" + Uri.encode("RealTrack Data Report") +
                    "&body=" + Uri.encode("Please find the CSV file of your recorded data attached with this email.");
                Uri uri = Uri.parse(uriText);
                sendEmailIntent.setData(uri);
                sendEmailIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/"
                        + fileName));
                startActivity(Intent.createChooser(sendEmailIntent, "Send mail..."));
                break;
        }

        return true;
    }

    private void updateParticipationSummaryList() {
        ProjectDAO projectDAO = new ProjectDAO(getApplicationContext());
        ActivitiesDAO activitiesDAO = new ActivitiesDAO(getApplicationContext());
        ParticipationDAO participationDao = new ParticipationDAO(getApplicationContext());

        projects_data = projectDAO.getAllProjects();
        DateFormat dateParser = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat timeParser = new SimpleDateFormat("hh:mm aaa");

        emailContent = new StringBuilder();
        emailContent.append("Data Report\n");
        emailContent.append("===========\n");


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(cacheOutputFile);
        } catch (FileNotFoundException e) {
        }

        String csvContent = "Project Title" + "," +
            "Project Start Date" + "," +
            "Project End Date" + "," +
            "Project Notes" + "," +
            "Activity Title" + "," +
            "Activity Start Date" + "," +
            "Activity End Date" + "," +
            "Activity Notes" + "," +
            "Activity Organizations" + "," +
            "Activity Communities" + "," +
            "Activity Initiatives" + "," +
            "Participation Date" + "," +
            "Participation Time" + "," +
            "Participation Men under 15" + "," +
            "Participation Men 15-24" + "," +
            "Participation Men over 24" + "," +
            "Participation Women under 15" + "," +
            "Participation Women 15-24" + "," +
            "Participation Women over 24" + "," +
            "Participation Event Details" + "\n";

        try {
            fos.write(csvContent.getBytes());
        } catch (IOException e) {
        }

        for (Project p : projects_data) {
            View childProjectView = getLayoutInflater().inflate(R.layout.row_projectsummary, null);
            TextView projectTitle = (TextView) childProjectView.findViewById(R.id.txtTitle);
            projectTitle.setText(p.getTitle());

            emailContent.append("----------------------" + "\n");
            emailContent.append("Project: " + p.getTitle() + "\n");
            emailContent.append("----------------------" + "\n");
            emailContent.append("  Start Date: " + dateParser.format(p.getStartDate()) + "\n");
            emailContent.append("  End Date: " + dateParser.format(p.getEndDate()) + "\n");

            ArrayList<Activities> activities_data = activitiesDAO.getAllActivitiesForProjectId(p.getId());

            for (Activities a : activities_data) {
                View childActivityView = getLayoutInflater().inflate(R.layout.row_activitiessummary, null);
                TextView activityTitle = (TextView) childActivityView.findViewById(R.id.txtTitle);
                activityTitle.setText(a.getTitle());

                emailContent.append("    ----------------------" + "\n");
                emailContent.append("    Activity: " + a.getTitle() + "\n");
                emailContent.append("    ----------------------" + "\n");
                emailContent.append("      Start Date: " + dateParser.format(a.getStartDate()) + "\n");
                emailContent.append("      End Date: " + dateParser.format(a.getEndDate()) + "\n");

                ArrayList<Participation> participation_data = participationDao.getAllParticipationsForActivityId(a.getId());

                if (participation_data.size() > 0) {
                    if (childProjectView.getParent() == null)
                        summaryLayout.addView(childProjectView);
                    summaryLayout.addView(childActivityView);
                }

                int sumMen = 0, sumWomen = 0;
                int sumMen1524 = 0, sumWomen1524 = 0;
                int sumMenOver24 = 0, sumWomenOver24 = 0;
                for (Participation participation : participation_data) {
                    View childParticipationView = getLayoutInflater().inflate(R.layout.row_allparticipation, null);
                    TextView participationDate = (TextView) childParticipationView.findViewById(R.id.date);
                    Date d = new Date(participation.getDate());
                    participationDate.setText(dateParser.format(d));
                    TextView participationMen = (TextView) childParticipationView.findViewById(R.id.men);
                    participationMen.setText(participation.getMen() + "");
                    participationMen = (TextView) childParticipationView.findViewById(R.id.men1524);
                    participationMen.setText(participation.getMen1524() + "");
                    participationMen = (TextView) childParticipationView.findViewById(R.id.menOver24);
                    participationMen.setText(participation.getMenOver24() + "");
                    TextView participationWomen = (TextView) childParticipationView.findViewById(R.id.women);
                    participationWomen.setText(participation.getWomen() + "");
                    participationWomen = (TextView) childParticipationView.findViewById(R.id.women1524);
                    participationWomen.setText(participation.getWomen1524() + "");
                    participationWomen = (TextView) childParticipationView.findViewById(R.id.womenOver24);
                    participationWomen.setText(participation.getWomenOver24() + "");
                    TextView participationNotes = (TextView) childParticipationView.findViewById(R.id.notes);
                    participationNotes.setText("Event details: " + participation.getNotes());
                    summaryLayout.addView(childParticipationView);

                    sumMen += participation.getMen();
                    sumWomen += participation.getWomen();
                    sumMen1524 += participation.getMen1524();
                    sumWomen1524 += participation.getWomen1524();
                    sumMenOver24 += participation.getMenOver24();
                    sumWomenOver24 += participation.getWomenOver24();

                    String[] initiativesList = a.getInitiatives().split("\\|");
                    String inits = "";
                    for (int i = 0; i < initiativesList.length; i++) {
                        if (initiativesList[i].equals("1"))
                            inits += AllInits[i] + "|";
                    }
                    inits = (inits.length() > 1) ? inits.substring(0, inits.length() - 1) : ""; // remove the last superfluous pipe character

                    csvContent = p.getTitle() + "," +
                        dateParser.format(p.getStartDate()) + "," +
                        dateParser.format(p.getEndDate()) + "," +
                        p.getNotes() + "," +
                        a.getTitle() + "," +
                        dateParser.format(a.getStartDate()) + "," +
                        dateParser.format(a.getEndDate()) + "," +
                        a.getNotes() + "," +
                        a.getOrgs() + "," +
                        a.getComms() + "," +
                        inits + "," +
                        dateParser.format(participation.getDate()) + "," +
                        timeParser.format(participation.getDate()) + "," +
                        participation.getMen() + "," +
                        participation.getMen1524() + "," +
                        participation.getMenOver24() + "," +
                        participation.getWomen() + "," +
                        participation.getWomen1524() + "," +
                        participation.getWomenOver24() + "," +
                        participation.getNotes() + "\n";
                    try {
                        fos.write(csvContent.getBytes());
                    } catch (IOException e) {
                    }

                }
                emailContent.append("      Total Participation: " + (sumMen + sumWomen) + "\n");
                emailContent.append("        Men under 15: " + sumMen + "\n");
                emailContent.append("        Men 15-24: " + sumMen1524 + "\n");
                emailContent.append("        Men over 24: " + sumMenOver24 + "\n");
                emailContent.append("        Women under 15: " + sumWomen + "\n");
                emailContent.append("        Women 15-24: " + sumWomen1524 + "\n");
                emailContent.append("        Women over 24: " + sumWomenOver24 + "\n");
            }
        }

        try {
            fos.close();
        } catch (IOException e) {
        }
    }

}