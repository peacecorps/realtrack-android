package com.hackforchange.views.participationsdonesummaries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.activities.ParticipantDAO;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.backend.projects.ProjectDAO;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.activities.Participant;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.models.projects.Project;
import com.hackforchange.providers.CachedFileContentProvider;

public class ParticipationSummaryActivity extends SherlockActivity {
  static final int SENDEMAIL_REQUEST = 1;
  public String[] allInits;
  
  private ArrayList<Project> projects_data;
  private StringBuilder emailContent;
  private LinearLayout summaryLayout;
  File cacheDir, cacheParticipationOutputFile, cacheDataOutputFile, nonAlignedDataOutputFile;
  String dataFileName, participationFileName;
  private int maxComms = 0;
  
  private final String ESCAPE_COMMAS = "\"";
  private final String COMMUNITY_DELIMITER = "@_@";
  
  private boolean dataToExportFound;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_participationsummary);
  }

  @Override
  public void onResume() {
    super.onResume();
    allInits = updateInitiativeNames();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    summaryLayout = (LinearLayout) findViewById(R.id.projectsummarylayout);
    summaryLayout.removeAllViews();
    DateFormat dateParser = new SimpleDateFormat("MMddyyyy");
    dataFileName = "RealTrack_Data_Report_" + dateParser.format(Calendar.getInstance().getTimeInMillis()) + ".csv";
    participationFileName = "RealTrack_Participation_Report_" + dateParser.format(Calendar.getInstance().getTimeInMillis()) + ".csv";
    cacheDir = getApplicationContext().getCacheDir(); // context being the Activity pointer
    cacheDataOutputFile = new File(cacheDir + File.separator + dataFileName);
    cacheParticipationOutputFile = new File(cacheDir + File.separator + participationFileName);
    nonAlignedDataOutputFile = new File(cacheDir + File.separator + "temp.csv");
    updateParticipationSummaryList();
  }

  private String[] updateInitiativeNames() {
    return new String[]{getResources().getString(R.string.wid), getResources().getString(R.string.youth),
      getResources().getString(R.string.malaria), getResources().getString(R.string.ecpa),
      getResources().getString(R.string.foodsecurity)};
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
        if(!dataToExportFound){
          Toast.makeText(getApplicationContext(), getResources().getString(R.string.noparticipationstoexport), Toast.LENGTH_SHORT).show();
          break;
        }
        final Intent sendEmailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sendEmailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        sendEmailIntent.setType("plain/text");
        String uriText = "mailto:" + Uri.encode("") +
                "?subject=" + Uri.encode("RealTrack Data Report") +
                "&body=" + Uri.encode("Please find the CSV file of your recorded data attached with this email.");
        Uri uri = Uri.parse(uriText);
        sendEmailIntent.setData(uri);
        
        ArrayList<Uri> uris = new ArrayList<Uri>();
        uris.add(Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/"
                + dataFileName));
        uris.add(Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/"
                + participationFileName));
        sendEmailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivity(Intent.createChooser(sendEmailIntent, "Send mail..."));
        overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        break;
    }

    return true;
  }

  private void updateParticipationSummaryList() {
    ProjectDAO projectDAO = new ProjectDAO(getApplicationContext());
    ActivitiesDAO activitiesDAO = new ActivitiesDAO(getApplicationContext());
    ParticipationDAO participationDAO = new ParticipationDAO(getApplicationContext());
    ParticipantDAO participantDAO = new ParticipantDAO(getApplicationContext());

    projects_data = projectDAO.getAllProjects();
    DateFormat dateParser = new SimpleDateFormat("MM/dd/yyyy");
    DateFormat timeParser = new SimpleDateFormat("hh:mm aaa");

    emailContent = new StringBuilder();
    emailContent.append("Data Report\n");
    emailContent.append("===========\n");


    FileOutputStream dataFos = null;
    FileOutputStream participationFos = null;
    try {
      dataFos = new FileOutputStream(nonAlignedDataOutputFile);
      participationFos = new FileOutputStream(cacheParticipationOutputFile);
    } catch (FileNotFoundException e) {
    }
    
    String dataCSVContent = "Project Title" + "," +
            "Project Start Date" + "," +
            "Project End Date" + "," +
            "Project Notes" + "," +
            "Activity Title" + "," +
            "Activity Start Date" + "," +
            "Activity End Date" + "," +
            "Activity Notes" + "," +
            "Activity Organizations" + "," +
            "Activity Community 1" + "," +
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

    String participationCSVContent = "Project Title" + "," +
            "Activity Title" + "," +
            "Participation Date" + "," +
            "Participation Time" + "," +
            "Participant Name" + "," +
            "Participant Phone Number" + "," +
            "Participant Village" + "," +
            "Participant Age" + "," +
            "Participant Gender" + "," +
            "Participation Event Details" + "\n";

    try {
      dataFos.write(dataCSVContent.getBytes());
      participationFos.write(participationCSVContent.getBytes());
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

        ArrayList<Participation> participation_data = participationDAO.getAllParticipationsForActivityId(a.getId());
        
        if(!participation_data.isEmpty())
          dataToExportFound = true;

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
          participationMen.setText(participation.getMenUnder15() + "");
          participationMen = (TextView) childParticipationView.findViewById(R.id.men1524);
          participationMen.setText(participation.getMen1524() + "");
          participationMen = (TextView) childParticipationView.findViewById(R.id.menOver24);
          participationMen.setText(participation.getMenOver24() + "");
          TextView participationWomen = (TextView) childParticipationView.findViewById(R.id.women);
          participationWomen.setText(participation.getWomenUnder15() + "");
          participationWomen = (TextView) childParticipationView.findViewById(R.id.women1524);
          participationWomen.setText(participation.getWomen1524() + "");
          participationWomen = (TextView) childParticipationView.findViewById(R.id.womenOver24);
          participationWomen.setText(participation.getWomenOver24() + "");
          TextView participationNotes = (TextView) childParticipationView.findViewById(R.id.notes);
          participationNotes.setText("Event details: " + participation.getNotes());
          summaryLayout.addView(childParticipationView);

          sumMen += participation.getMenUnder15();
          sumWomen += participation.getWomenUnder15();
          sumMen1524 += participation.getMen1524();
          sumWomen1524 += participation.getWomen1524();
          sumMenOver24 += participation.getMenOver24();
          sumWomenOver24 += participation.getWomenOver24();

          String[] initiativesList = a.getInitiatives().split("\\|");
          String inits = "";
          for (int i = 0; i < initiativesList.length; i++) {
            if (initiativesList[i].equals("1"))
              inits += allInits[i] + "|";
          }
          inits = (inits.length() > 1) ? inits.substring(0, inits.length() - 1) : ""; // remove the last superfluous pipe character

          int currentComms = findNumberOfCommunities(a.getComms());
          if(currentComms > maxComms)
            maxComms = currentComms;

          dataCSVContent = ESCAPE_COMMAS + p.getTitle() + ESCAPE_COMMAS + "," +
                  dateParser.format(p.getStartDate()) + "," +
                  dateParser.format(p.getEndDate()) + "," +
                  ESCAPE_COMMAS + p.getNotes() + ESCAPE_COMMAS + "," +
                  ESCAPE_COMMAS + a.getTitle() + ESCAPE_COMMAS + "," +
                  dateParser.format(a.getStartDate()) + "," +
                  dateParser.format(a.getEndDate()) + "," +
                  ESCAPE_COMMAS + a.getNotes() + ESCAPE_COMMAS + "," +
                  ESCAPE_COMMAS + a.getOrgs() + ESCAPE_COMMAS + "," +
                  COMMUNITY_DELIMITER + a.getComms() + COMMUNITY_DELIMITER +
                  inits + "," +
                  dateParser.format(participation.getDate()) + "," +
                  timeParser.format(participation.getDate()) + "," +
                  participation.getMenUnder15() + "," +
                  participation.getMen1524() + "," +
                  participation.getMenOver24() + "," +
                  participation.getWomenUnder15() + "," +
                  participation.getWomen1524() + "," +
                  participation.getWomenOver24() + "," +
                  ESCAPE_COMMAS + participation.getNotes() + ESCAPE_COMMAS + "\n";
          try {
            dataFos.write(dataCSVContent.getBytes());
          } catch (IOException e) {
          }
          
          List<Participant> participantList = participantDAO.getAllParticipantsForParticipationId(participation.getId());
          for(Participant participant: participantList){
            participationCSVContent = ESCAPE_COMMAS + p.getTitle() + ESCAPE_COMMAS + "," +
                    ESCAPE_COMMAS + a.getTitle() + ESCAPE_COMMAS + "," +
                    dateParser.format(participation.getDate()) + "," +
                    timeParser.format(participation.getDate()) + "," +
                    ESCAPE_COMMAS + participant.getName() + ESCAPE_COMMAS + "," +
                    ESCAPE_COMMAS + participant.getPhoneNumber() + ESCAPE_COMMAS + "," +
                    ESCAPE_COMMAS + participant.getVillage() + ESCAPE_COMMAS + "," +
                    participant.getAge() + "," +
                    (participant.getGender()==Participant.MALE? "Male" : "Female") + "," +
                    ESCAPE_COMMAS + participation.getNotes() + ESCAPE_COMMAS + "\n";
            try {
              participationFos.write(participationCSVContent.getBytes());
            } catch (IOException e) {
            }
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
      dataFos.close();
      participationFos.close();
    } catch (IOException e) {
    }
    
    normalizeCSVColumns(); //required if the user enters multiple communities separated by commas

  }

  private void normalizeCSVColumns() {
    BufferedReader fRead = null;
    FileInputStream fis = null;
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(cacheDataOutputFile);
      fis = new FileInputStream(nonAlignedDataOutputFile);
      fRead = new BufferedReader(new InputStreamReader(fis));
    } catch (FileNotFoundException e) {
    }
    
    try {
      //handle CSV column headings
      int numCommasToAdd = maxComms;
      String s = fRead.readLine();
      String[] separatedStrings = s.split("Activity Community 1,");
      String stringToWrite = separatedStrings[0];
      for(int i=0; i<numCommasToAdd; i++)
        stringToWrite += "Activity Community "+(i+1)+",";
      stringToWrite += separatedStrings[1] + "\n";
      fos.write(stringToWrite.getBytes());
      
      //handle the actual data
      while(null != (s = fRead.readLine())){
        separatedStrings = s.split(COMMUNITY_DELIMITER);
        stringToWrite = separatedStrings[0] + separatedStrings[1];
        int currentComms = findNumberOfCommunities(separatedStrings[1]);
        numCommasToAdd = 1;
        if(currentComms < maxComms)
          numCommasToAdd = maxComms-currentComms+1;
        for(int i=0; i<numCommasToAdd; i++)
          stringToWrite += ",";
        stringToWrite += separatedStrings[2] + "\n";
        fos.write(stringToWrite.getBytes());
      }
    }
    catch (IOException e) {
    }
    
    try {
      fis.close();
      fRead.close();
      fos.close();
    } catch (IOException e) {
    }
  }

  private int findNumberOfCommunities(String comms) {
    int numCommas = 0;
    for(int i=0; i<comms.length(); i++){
      if(comms.charAt(i) == ',')
        numCommas++;
    }
    return numCommas + 1;
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    deleteTemporaryFiles();
    finish();
  }

  private void deleteTemporaryFiles() {
    cacheDataOutputFile.delete();
    cacheParticipationOutputFile.delete();
  }
  
  @Override
  public void onPause() {
    super.onPause();
    deleteTemporaryFiles();
  }

}