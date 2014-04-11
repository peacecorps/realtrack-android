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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask.Status;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ParticipationSummaryActivity extends SherlockActivity {
  static final int SENDEMAIL_REQUEST = 1;
  private static final int PROGRESS_DIALOG = 2;
  
  private LinearLayout summaryLayout;

  private int maxComms = 0;

  private final String ESCAPE_COMMAS = "\"";
  private final String COMMUNITY_DELIMITER = "@_@";

  private boolean dataToExportFound;
  private DataHolder dataHolder;

  DateFormat dateParser, timeParser;
  private File nonAlignedDataOutputFile;
  private File signInReportsOutputFile;
  private File cacheParticipationOutputFile;
  private File cacheDataOutputFile;
  public String signInReportsFileName;
  public String participationFileName;
  public String dataFileName;

  private SendEmailTask sendEmailTask;
  private ProgressDialog progressDialog;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_participationsummary);
    dateParser = new SimpleDateFormat("MM/dd/yyyy");
    timeParser = new SimpleDateFormat("hh:mm aaa");
    
    SendEmailTask task = (SendEmailTask) getLastNonConfigurationInstance();
    if(task!=null){
      sendEmailTask = task;
      sendEmailTask.reAttach(this);
      if(sendEmailTask.getStatus()==Status.RUNNING){
        showDialog(PROGRESS_DIALOG);
        signInReportsFileName = sendEmailTask.signInReportsFileName;
        participationFileName = sendEmailTask.participationFileName;
        dataFileName = sendEmailTask.dataFileName;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    dataHolder = createDataHolder();
    updateDisplay(dataHolder);
  }

  private DataHolder createDataHolder() {
    ProjectDAO projectDAO = new ProjectDAO(getApplicationContext());
    ActivitiesDAO activitiesDAO = new ActivitiesDAO(getApplicationContext());
    ParticipationDAO participationDAO = new ParticipationDAO(getApplicationContext());
    ParticipantDAO participantDAO = new ParticipantDAO(getApplicationContext());

    DataHolder dHolder = new DataHolder();
    List<ProjectHolder> pHolder_data = new ArrayList<ProjectHolder>();

    List<Project> projects_data = projectDAO.getAllProjects();

    for(Project p: projects_data){
      ProjectHolder pHolder = new ProjectHolder();
      pHolder.p = p;
      List<ActivityHolder> aHolder_data = new ArrayList<ActivityHolder>();

      List<Activities> activities_data = activitiesDAO.getAllActivitiesForProjectId(p.getId());
      for(Activities a: activities_data){
        ActivityHolder aHolder = new ActivityHolder();
        aHolder.a = a;
        List<ParticipationHolder> paHolder_data = new ArrayList<ParticipationHolder>();

        List<Participation> participation_data =participationDAO.getAllParticipationsForActivityId(a.getId());
        for(Participation pa: participation_data){
          ParticipationHolder paHolder = new ParticipationHolder();
          paHolder.p = pa;
          paHolder.participantList = participantDAO.getAllParticipantsForParticipationId(pa.getId());
          paHolder_data.add(paHolder);
        }
        aHolder.participationHolderList = paHolder_data;

        aHolder_data.add(aHolder);
      }
      pHolder.activityHolderList = aHolder_data;

      pHolder_data.add(pHolder);
    }
    dHolder.pHolder_data = pHolder_data;

    return dHolder;
  }

  /**
   * Only updates UI elements. Does not create any email related items.
   * @param dHolder DataHolder object containing details of current state of projects, 
   *                activities, participations, and participants.
   */
  private void updateDisplay(DataHolder dHolder) {
    summaryLayout = (LinearLayout) findViewById(R.id.projectsummarylayout);
    summaryLayout.removeAllViews();

    for (ProjectHolder pHolder : dHolder.pHolder_data) {
      Project p = pHolder.p;

      View childProjectView = getLayoutInflater().inflate(R.layout.row_projectsummary, null);
      TextView projectTitle = (TextView) childProjectView.findViewById(R.id.txtTitle);
      projectTitle.setText(p.getTitle());

      for (ActivityHolder aHolder : pHolder.activityHolderList) {
        Activities a = aHolder.a;
        View childActivityView = getLayoutInflater().inflate(R.layout.row_activitiessummary, null);
        TextView activityTitle = (TextView) childActivityView.findViewById(R.id.txtTitle);
        activityTitle.setText(a.getTitle());

        if (aHolder.participationHolderList.size() > 0) {
          if (childProjectView.getParent() == null)
            summaryLayout.addView(childProjectView);
          summaryLayout.addView(childActivityView);
        }

        if(!aHolder.participationHolderList.isEmpty())
          dataToExportFound = true;

        for (ParticipationHolder paHolder : aHolder.participationHolderList) {
          Participation participation = paHolder.p;

          View childParticipationView = getLayoutInflater().inflate(R.layout.row_allparticipation, null);
          TextView participationDate = (TextView) childParticipationView.findViewById(R.id.date);
          Date d = new Date(participation.getDate());
          participationDate.setText(dateParser.format(d));
          TextView participationMen = (TextView) childParticipationView.findViewById(R.id.men);
          participationMen.setText(Integer.toString(participation.getMenUnder15()));
          participationMen = (TextView) childParticipationView.findViewById(R.id.men1524);
          participationMen.setText(Integer.toString(participation.getMen1524()));
          participationMen = (TextView) childParticipationView.findViewById(R.id.menOver24);
          participationMen.setText(Integer.toString(participation.getMenOver24()));
          TextView participationWomen = (TextView) childParticipationView.findViewById(R.id.women);
          participationWomen.setText(Integer.toString(participation.getWomenUnder15()));
          participationWomen = (TextView) childParticipationView.findViewById(R.id.women1524);
          participationWomen.setText(Integer.toString(participation.getWomen1524()));
          participationWomen = (TextView) childParticipationView.findViewById(R.id.womenOver24);
          participationWomen.setText(Integer.toString(participation.getWomenOver24()));
          TextView participationNotes = (TextView) childParticipationView.findViewById(R.id.notes);
          participationNotes.setText("Event details: " + participation.getNotes());

          summaryLayout.addView(childParticipationView);
        }
      }
    }
  }

  private String[] updateInitiativeNames() {
    return new String[]{getResources().getString(R.string.wid), getResources().getString(R.string.youth),
            getResources().getString(R.string.malaria), getResources().getString(R.string.ecpa),
            getResources().getString(R.string.foodsecurity)};
  }

  public void prepareEmailInBackground() {
    DateFormat dateForFileNameParser = new SimpleDateFormat("MMddyyyy");
    dataFileName = "RealTrack_Data_Report_" + dateForFileNameParser.format(Calendar.getInstance().getTimeInMillis()) + ".csv";
    participationFileName = "RealTrack_Participation_Report_" + dateForFileNameParser.format(Calendar.getInstance().getTimeInMillis()) + ".csv";
    signInReportsFileName = "RealTrack_SignIn_Report_" + dateForFileNameParser.format(Calendar.getInstance().getTimeInMillis()) + ".pdf";
    File cacheDir = getApplicationContext().getCacheDir(); // context being the Activity pointer
    cacheDataOutputFile = new File(cacheDir + File.separator + dataFileName);
    cacheParticipationOutputFile = new File(cacheDir + File.separator + participationFileName);
    signInReportsOutputFile = new File(cacheDir + File.separator + signInReportsFileName);
    nonAlignedDataOutputFile = new File(cacheDir + File.separator + "temp.csv");
    
    //will be used in case this activity is destroyed and recreated e.g. on rotation, keyboard popup etc
    sendEmailTask.dataFileName = dataFileName;
    sendEmailTask.participationFileName = participationFileName;
    sendEmailTask.signInReportsFileName = signInReportsFileName;
    
    createEmail(dataHolder);
  }

  /**
   * Creates files to email.
   * @param dHolder
   */
  private void createEmail(DataHolder dHolder){
    String[] allInits = updateInitiativeNames();

    FileOutputStream dataFos = null;
    FileOutputStream participationFos = null;
    FileOutputStream signinFos = null;
    Document signinDocument = null;
    try {
      dataFos = new FileOutputStream(nonAlignedDataOutputFile);
      participationFos = new FileOutputStream(cacheParticipationOutputFile);
      signinFos = new FileOutputStream(signInReportsOutputFile);
      signinDocument = new Document();
      PdfWriter.getInstance(signinDocument, signinFos);
      signinDocument.open();
      signinDocument.addTitle("RealTrack Sign-In Report");
      Paragraph reportHeader = new Paragraph("RealTrack Sign-In Report"); 
      reportHeader.add(new Paragraph("Report generated on: "+(new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date()))));
      addNewLines(reportHeader, 2);
      signinDocument.add(reportHeader);
    } catch (Exception e) {
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

    for (ProjectHolder pHolder : dHolder.pHolder_data) {
      Project p = pHolder.p;

      for (ActivityHolder aHolder : pHolder.activityHolderList) {
        Activities a = aHolder.a;

        for (ParticipationHolder paHolder : aHolder.participationHolderList) {
          Participation participation = paHolder.p;

          Date d = new Date(participation.getDate());

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

          Paragraph projectParagraph = null;
          PdfPTable table = null;

          if(!paHolder.participantList.isEmpty()){
            projectParagraph = new Paragraph();
            addNewLines(projectParagraph, 1);
            projectParagraph.add(new Paragraph("Project Title: " + p.getTitle()));
            addNewLines(projectParagraph, 1);
            projectParagraph.add(new Paragraph("Activity Title: " + a.getTitle()));
            addNewLines(projectParagraph, 1);
            projectParagraph.add(new Paragraph("Sign-In Sheet for: " + dateParser.format(d)+" " + timeParser.format(d)));
            projectParagraph.add(new Paragraph("Event details: " + participation.getNotes()));
            addNewLines(projectParagraph, 2);

            table = new PdfPTable(5);

            PdfPCell c1 = new PdfPCell(new Phrase("Name"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Phone"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Village"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Age"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Gender"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            table.setHeaderRows(1);
          }

          for(Participant participant: paHolder.participantList){
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

            table.addCell(participant.getName());
            table.addCell(participant.getPhoneNumber());
            table.addCell(participant.getVillage());
            table.addCell(Integer.toString(participant.getAge()));
            table.addCell(participant.getGender()==Participant.MALE? "Male" : "Female");

            try {
              participationFos.write(participationCSVContent.getBytes());
            } catch (IOException e) {
            }
          }

          if(projectParagraph != null){
            projectParagraph.add(table);
            try {
              signinDocument.add(projectParagraph);
              signinDocument.newPage();
            }
            catch (DocumentException e) {
            }
          }
        }
      }
    }

    try {
      dataFos.close();
      participationFos.close();
      signinDocument.close();
    } catch (IOException e) {
    }

    normalizeCSVColumns(); //required if the user enters multiple communities separated by commas
  }

  private void addNewLines(Paragraph paragraph, int numLinesToAdd) {
    for(int i=0;i<numLinesToAdd;++i)
      paragraph.add(new Paragraph(""));
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
    deleteFileIfNotNull(cacheDataOutputFile);
    deleteFileIfNotNull(cacheParticipationOutputFile);
    deleteFileIfNotNull(nonAlignedDataOutputFile);
    deleteFileIfNotNull(signInReportsOutputFile);
  }

  private void deleteFileIfNotNull(File fileToDelete){
    if(fileToDelete != null) fileToDelete.delete();
  }

  /**
   * Class encapsulating details of current state of projects, 
   * activities, participations, and participants.
   * @author Raj
   */
  private class DataHolder{
    List<ProjectHolder> pHolder_data;
  }

  private class ProjectHolder{
    Project p;
    List<ActivityHolder> activityHolderList;
  }

  private class ActivityHolder{
    Activities a;
    List<ParticipationHolder> participationHolderList;
  }

  private class ParticipationHolder{
    Participation p;
    List<Participant> participantList;
  }

  //create actionbar menu
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
        showDialog(PROGRESS_DIALOG);
        
        if(sendEmailTask == null || sendEmailTask.getStatus()==Status.FINISHED)
          sendEmailTask = new SendEmailTask(this);
        sendEmailTask.execute();
        break;
    }

    return true;
  }

  /**
   * Callback for SendEmailTask
   * @param dataFileName 
   * @param participationFileName 
   * @param signInReportsFileName 
   */
  public void sendEmail(){
    if(progressDialog.isShowing())
      progressDialog.dismiss();
    
    final Intent sendEmailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    sendEmailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
    sendEmailIntent.setType("plain/text");
    String uriText = "mailto:" + Uri.encode("") +
            "?subject=" + Uri.encode("RealTrack Data Report") +
            "&body=" + Uri.encode("Please find your data attached with this email.");
    Uri uri = Uri.parse(uriText);
    sendEmailIntent.setData(uri);

    ArrayList<Uri> uris = new ArrayList<Uri>();
    uris.add(Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/"
            + dataFileName));
    uris.add(Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/"
            + participationFileName));
    uris.add(Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/"
            + signInReportsFileName));
    sendEmailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

    startActivityForResult(Intent.createChooser(sendEmailIntent, "Send mail..."), ParticipationSummaryActivity.SENDEMAIL_REQUEST);
    overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == SENDEMAIL_REQUEST) {
      deleteTemporaryFiles(); //delete files irrespective of whether REQUEST_OK or not
    }
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    return sendEmailTask;
  }
  
  @Override
  protected Dialog onCreateDialog(int id) {
    switch(id){
      case PROGRESS_DIALOG:
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating Reports");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    return super.onCreateDialog(id);
  }

}