package com.realtrackandroid.views.participationsdonesummaries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ActivitiesDAO;
import com.realtrackandroid.backend.activities.ParticipantDAO;
import com.realtrackandroid.backend.activities.ParticipationDAO;
import com.realtrackandroid.backend.projects.ProjectDAO;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.activities.Participant;
import com.realtrackandroid.models.activities.Participation;
import com.realtrackandroid.models.projects.Project;
import com.realtrackandroid.providers.CachedFileContentProvider;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.GlossaryDialog;
import com.realtrackandroid.views.help.HelpDialog;

public class ParticipationSummaryActivity extends SherlockFragmentActivity {
  private static final int SENDEMAIL_REQUEST = 1;

  private static final int SENDBT_REQUEST = 2;

  private static final int PROGRESS_DIALOG = 3;

  private static final Font TITLE_FONT = new Font(FontFamily.HELVETICA, 18);

  private int maxComms = 0;

  private final String ESCAPE_COMMAS = "\"";

  private final String COMMUNITY_DELIMITER = "@_@";

  private boolean dataToExportFound, signaturesToExportFound;

  private DataHolder dataHolder;

  DateFormat dateParser, timeParser;

  private File nonAlignedDataOutputFile;

  private File signInReportsOutputFile;

  private File cacheParticipationOutputFile;

  private File cacheDataOutputFile;

  public String signInReportsFileName;

  public String participationFileName;

  public String dataFileName;

  private SendDataTask sendEmailTask;

  private ProgressDialog progressDialog;

  private XYSeries mCurrentSeries;

  private XYMultipleSeriesDataset mDataset;

  private GraphicalView mChartView;

  private XYMultipleSeriesRenderer mRenderer;

  private ParticipationSummaryListAdapter participationSummaryListAdapter;

  private ExpandableListView projectsummaryExpandableListView;

  private boolean use_email_not_bt;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_participationsummary);
    dateParser = new SimpleDateFormat("MM/dd/yyyy");
    timeParser = new SimpleDateFormat("hh:mm aaa");

    SendDataTask task = (SendDataTask) getLastCustomNonConfigurationInstance();
    
    if (task != null) {
      sendEmailTask = task;
      sendEmailTask.reAttach(this);
      if (sendEmailTask.getStatus() == Status.RUNNING) {
        showDialog(PROGRESS_DIALOG);
        signInReportsFileName = sendEmailTask.signInReportsFileName;
        participationFileName = sendEmailTask.participationFileName;
        dataFileName = sendEmailTask.dataFileName;
        use_email_not_bt = sendEmailTask.use_email_not_bt;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    dataHolder = createDataHolder();
    if (mChartView == null)
      updateDisplay(dataHolder);
    else
      mChartView.repaint();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // save the current data, for instance when changing screen orientation
    outState.putSerializable("dataset", mDataset);
    outState.putSerializable("renderer", mRenderer);
    outState.putSerializable("current_series", mCurrentSeries);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedState) {
    super.onRestoreInstanceState(savedState);
    // restore the current data, for instance when changing the screen orientation
    mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
    mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
    mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
  }

  private DataHolder createDataHolder() {
    ProjectDAO projectDAO = new ProjectDAO(getApplicationContext());
    ActivitiesDAO activitiesDAO = new ActivitiesDAO(getApplicationContext());
    ParticipationDAO participationDAO = new ParticipationDAO(getApplicationContext());
    ParticipantDAO participantDAO = new ParticipantDAO(getApplicationContext());

    DataHolder dHolder = new DataHolder();
    List<ProjectHolder> pHolder_data = new ArrayList<ProjectHolder>();

    List<Project> projects_data = projectDAO.getAllProjects();

    for (Project p : projects_data) {
      ProjectHolder pHolder = new ProjectHolder();
      pHolder.p = p;
      List<ActivityHolder> aHolder_data = new ArrayList<ActivityHolder>();

      List<Activities> activities_data = activitiesDAO.getAllActivitiesForProjectId(p.getId());
      for (Activities a : activities_data) {
        ActivityHolder aHolder = new ActivityHolder();
        aHolder.a = a;
        List<ParticipationHolder> paHolder_data = new ArrayList<ParticipationHolder>();

        List<Participation> participation_data = participationDAO
                .getServicedParticipationsForActivityId(a.getId());
        for (Participation pa : participation_data) {
          dataToExportFound = true;
          ParticipationHolder paHolder = new ParticipationHolder();
          paHolder.pa = pa;
          paHolder.participantList = participantDAO
                  .getAllParticipantsForParticipationId(pa.getId());
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
   * 
   * @param dHolder
   *          DataHolder object containing details of current state of projects, activities,
   *          participations, and participants.
   */
  private void updateDisplay(DataHolder dHolder) {
    projectsummaryExpandableListView = (ExpandableListView) findViewById(R.id.projectsummaryListView);

    participationSummaryListAdapter = new ParticipationSummaryListAdapter(this,
            R.layout.row_projectsummary, R.layout.row_activitysummary,
            projectsummaryExpandableListView, dHolder.pHolder_data);
    participationSummaryListAdapter.setInflater((getLayoutInflater()));
    projectsummaryExpandableListView.setAdapter(participationSummaryListAdapter);

    // make sure all groups are expanded by default
    for (int i = 0; i < dHolder.pHolder_data.size(); i++) {
      projectsummaryExpandableListView.expandGroup(i);
    }

    // hide default arrow group indicator because we will provide our own
    projectsummaryExpandableListView.setGroupIndicator(null);
  }

  private String[] updateCsppNames() {
    return new String[] { getResources().getString(R.string.genderequalityandwomensempowerment),
        getResources().getString(R.string.hivaids),
        getResources().getString(R.string.technologyfordevelopment),
        getResources().getString(R.string.youthasresources),
        getResources().getString(R.string.volunteerism),
        getResources().getString(R.string.peoplewithdisabilities) };
  }

  private String[] updateInitiativeNames() {
    return new String[] { getResources().getString(R.string.malaria),
        getResources().getString(R.string.ecpa), getResources().getString(R.string.foodsecurity) };
  }

  /**
   * Callback for SendDataTask doInBackground()
   */
  public void prepareDataInBackgroundCallback() {
    DateFormat dateForFileNameParser = new SimpleDateFormat("MMddyyyy");
    dataFileName = "RealTrack_Data_Report_"
            + dateForFileNameParser.format(Calendar.getInstance().getTimeInMillis()) + ".csv";
    participationFileName = "RealTrack_Participation_Report_"
            + dateForFileNameParser.format(Calendar.getInstance().getTimeInMillis()) + ".csv";
    signInReportsFileName = "RealTrack_SignIn_Report_"
            + dateForFileNameParser.format(Calendar.getInstance().getTimeInMillis()) + ".pdf";
    File cacheDir = getApplicationContext().getCacheDir(); // context being the Activity pointer
    cacheDataOutputFile = new File(cacheDir + File.separator + dataFileName);
    cacheParticipationOutputFile = new File(cacheDir + File.separator + participationFileName);
    signInReportsOutputFile = new File(cacheDir + File.separator + signInReportsFileName);
    nonAlignedDataOutputFile = new File(cacheDir + File.separator + "temp.csv");

    // will be used in case this activity is destroyed and recreated e.g. on rotation, keyboard
    // popup etc
    sendEmailTask.dataFileName = dataFileName;
    sendEmailTask.participationFileName = participationFileName;
    sendEmailTask.signInReportsFileName = signInReportsFileName;
    sendEmailTask.use_email_not_bt = use_email_not_bt;

    createDataFiles(dataHolder);
  }

  /**
   * Creates files to email.
   * 
   * @param dHolder
   */
  private void createDataFiles(DataHolder dHolder) {
    String[] allInits = updateInitiativeNames();
    String[] allCspps = updateCsppNames();

    FileOutputStream dataFos = null;
    FileOutputStream participationFos = null;
    FileOutputStream signinFos = null;
    Document signinDocument = null;
    try {
      dataFos = new FileOutputStream(nonAlignedDataOutputFile);
      participationFos = new FileOutputStream(cacheParticipationOutputFile);
      signinFos = new FileOutputStream(signInReportsOutputFile);
      signinDocument = new Document(PageSize.A4);
      PdfWriter.getInstance(signinDocument, signinFos);
      signinDocument.open();
      signinDocument.addTitle("RealTrack Sign-In Report");
      Paragraph reportHeader = new Paragraph("RealTrack Sign-In Report", TITLE_FONT);
      reportHeader.add(new Paragraph("Report generated on: "
              + (new SimpleDateFormat("MM/dd/yyyy hh:mm aaa").format(new Date()))));
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      if (prefs.contains(getString(R.string.name)))
        reportHeader
                .add(new Paragraph("PCV Name: " + prefs.getString(getString(R.string.name), "")));
      signinDocument.add(reportHeader);
      LineSeparator ls = new LineSeparator();
      signinDocument.add(new Chunk(ls));
    }
    catch (Exception e) {
    }

    String dataCSVContent = "Project Title" + "," + "Project Start Date" + "," + "Project End Date"
            + "," + "Project Notes" + "," + "Activity Title" + "," + "Activity Start Date" + ","
            + "Activity End Date" + "," + "Activity Cohort" + "," + "Activity Notes" + ","
            + "Activity Organizations" + "," + "Activity Community 1" + "," + "Activity CSPP" + ","
            + "Activity Initiatives" + "," + "Participation Date" + "," + "Participation Time"
            + "," + "Participation Men 0-9" + "," + "Participation Men 10-17" + ","
            + "Participation Men 18-24" + "," + "Participation Men over 25" + ","
            + "Participation Women 0-9" + "," + "Participation Women 10-17" + ","
            + "Participation Women 18-24" + "," + "Participation Women over 25" + ","
            + "Service Providers Men 0-9" + "," + "Service Providers Men 10-17" + ","
            + "Service Providers Men 18-24" + "," + "Service Providers Men over 25" + ","
            + "Service Providers Women 0-9" + "," + "Service Providers Women 10-17" + ","
            + "Service Providers Women 18-24" + "," + "Service Providers Women over 25" + ","
            + "Participation Event Details" + "\n";

    String participationCSVContent = "Project Title" + "," + "Activity Title" + "," + "Cohort Name"
            + "," + "Participation Date" + "," + "Participation Time" + "," + "Participant Name"
            + "," + "Participant Phone Number" + "," + "Participant Village" + ","
            + "Participant Age" + "," + "Participant Gender" + "," + "Participation Event Details"
            + "\n";

    try {
      dataFos.write(dataCSVContent.getBytes());
      participationFos.write(participationCSVContent.getBytes());
    }
    catch (IOException e) {
    }

    for (ProjectHolder pHolder : dHolder.pHolder_data) {
      Project p = pHolder.p;

      for (ActivityHolder aHolder : pHolder.activityHolderList) {
        Activities a = aHolder.a;

        for (ParticipationHolder paHolder : aHolder.participationHolderList) {
          Participation participation = paHolder.pa;

          Date d = new Date(participation.getDate());

          String[] csppList = a.getCspp().split("\\|");
          String cspp = "";
          for (int i = 0; i < csppList.length; i++) {
            if (csppList[i].equals("1"))
              cspp += allCspps[i] + "|";
          }
          cspp = (cspp.length() > 1) ? cspp.substring(0, cspp.length() - 1) : ""; // remove the last
                                                                                  // superfluous
                                                                                  // pipe character

          String[] initiativesList = a.getInitiatives().split("\\|");
          String inits = "";
          for (int i = 0; i < initiativesList.length; i++) {
            if (initiativesList[i].equals("1"))
              inits += allInits[i] + "|";
          }
          inits = (inits.length() > 1) ? inits.substring(0, inits.length() - 1) : ""; // remove the
                                                                                      // last
                                                                                      // superfluous
                                                                                      // pipe
                                                                                      // character

          int currentComms = findNumberOfCommunities(a.getComms());
          if (currentComms > maxComms)
            maxComms = currentComms;

          dataCSVContent = ESCAPE_COMMAS + p.getTitle() + ESCAPE_COMMAS + ","
                  + dateParser.format(p.getStartDate()) + "," + dateParser.format(p.getEndDate())
                  + "," + ESCAPE_COMMAS + p.getNotes() + ESCAPE_COMMAS + "," + ESCAPE_COMMAS
                  + a.getTitle() + ESCAPE_COMMAS + "," + dateParser.format(a.getStartDate()) + ","
                  + dateParser.format(a.getEndDate()) + "," + ESCAPE_COMMAS + a.getCohort()
                  + ESCAPE_COMMAS + "," + ESCAPE_COMMAS + a.getNotes() + ESCAPE_COMMAS + ","
                  + ESCAPE_COMMAS + a.getOrgs() + ESCAPE_COMMAS + "," + COMMUNITY_DELIMITER
                  + a.getComms() + COMMUNITY_DELIMITER + cspp + "," + inits + ","
                  + dateParser.format(participation.getDate()) + ","
                  + timeParser.format(participation.getDate()) + "," + participation.getMen09()
                  + "," + participation.getMen1017() + "," + participation.getMen1824() + ","
                  + participation.getMenOver25() + "," + participation.getWomen09() + ","
                  + participation.getWomen1017() + "," + participation.getWomen1824() + ","
                  + participation.getWomenOver25() + "," + participation.getSpMen09() + ","
                  + participation.getSpMen1017() + "," + participation.getSpMen1824() + ","
                  + participation.getSpMenOver25() + "," + participation.getSpWomen09() + ","
                  + participation.getSpWomen1017() + "," + participation.getSpWomen1824() + ","
                  + participation.getSpWomenOver25() + "," + ESCAPE_COMMAS
                  + participation.getNotes() + ESCAPE_COMMAS + "\n";
          try {
            dataFos.write(dataCSVContent.getBytes());
          }
          catch (IOException e) {
          }

          Paragraph projectParagraph = null;
          PdfPTable table = null;

          if (!paHolder.participantList.isEmpty()) {
            signaturesToExportFound = true;
            projectParagraph = new Paragraph();
            addNewLines(projectParagraph, 1);
            projectParagraph.add(new Paragraph("Project Title: " + p.getTitle()));
            projectParagraph.add(new Paragraph("Activity Title: " + a.getTitle()));
            projectParagraph.add(new Paragraph("Reporting Cohort: " + a.getCohort()));
            projectParagraph.add(new Paragraph("Sign-In Sheet for: " + dateParser.format(d) + " "
                    + timeParser.format(d)));
            projectParagraph.add(new Paragraph("Event details: " + participation.getNotes()));
            projectParagraph.add(new Paragraph("Sign-in Sheet:"));
            addNewLines(projectParagraph, 1);

            table = new PdfPTable(new float[] { 2, 2, 2, 1, 1, 3 });
            table.setWidthPercentage(100f);

            PdfPCell c1 = new PdfPCell(new Phrase("Name"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Phone"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Village"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Age"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Gender"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Signature"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(c1);

            table.setHeaderRows(1);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
          }

          for (Participant participant : paHolder.participantList) {
            participationCSVContent = ESCAPE_COMMAS + p.getTitle() + ESCAPE_COMMAS + ","
                    + ESCAPE_COMMAS + a.getTitle() + ESCAPE_COMMAS + "," + ESCAPE_COMMAS
                    + a.getCohort() + ESCAPE_COMMAS + ","
                    + dateParser.format(participation.getDate()) + ","
                    + timeParser.format(participation.getDate()) + "," + ESCAPE_COMMAS
                    + participant.getName() + ESCAPE_COMMAS + "," + ESCAPE_COMMAS
                    + participant.getPhoneNumber() + ESCAPE_COMMAS + "," + ESCAPE_COMMAS
                    + participant.getVillage() + ESCAPE_COMMAS + "," + participant.getAge() + ","
                    + (participant.getGender() == Participant.MALE ? "Male" : "Female") + ","
                    + ESCAPE_COMMAS + participation.getNotes() + ESCAPE_COMMAS + "\n";

            table.addCell(participant.getName());
            table.addCell(participant.getPhoneNumber());
            table.addCell(participant.getVillage());
            table.addCell(Integer.toString(participant.getAge()));
            table.addCell(participant.getGender() == Participant.MALE ? "Male" : "Female");
            try {
              Image signatureImage = Image.getInstance(participant.getSignaturePath());
              PdfPCell imageCell = new PdfPCell(signatureImage);
              imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
              imageCell.setVerticalAlignment(Element.ALIGN_CENTER);
              imageCell.setFixedHeight(50);
              table.addCell(imageCell);
            }
            catch (BadElementException e1) {
              table.addCell("Signature not found");
            }
            catch (MalformedURLException e1) {
              table.addCell("Signature not found");
            }
            catch (IOException e1) {
              table.addCell("Signature not found");
            }

            try {
              participationFos.write(participationCSVContent.getBytes());
            }
            catch (IOException e) {
            }
          }

          if (projectParagraph != null) {
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
    }
    catch (IOException e) {
    }

    normalizeCSVColumns(); // required if the user enters multiple communities separated by commas
  }

  private void addNewLines(Paragraph paragraph, int numLinesToAdd) {
    for (int i = 0; i < numLinesToAdd; ++i)
      paragraph.add(new Paragraph(" "));
  }

  private void normalizeCSVColumns() {
    BufferedReader fRead = null;
    FileInputStream fis = null;
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(cacheDataOutputFile);
      fis = new FileInputStream(nonAlignedDataOutputFile);
      fRead = new BufferedReader(new InputStreamReader(fis));
    }
    catch (FileNotFoundException e) {
    }

    try {
      // handle CSV column headings
      int numCommasToAdd = maxComms;
      String s = fRead.readLine();
      String[] separatedStrings = s.split("Activity Community 1,");
      String stringToWrite = separatedStrings[0];
      for (int i = 0; i < numCommasToAdd; i++)
        stringToWrite += "Activity Community " + (i + 1) + ",";
      stringToWrite += separatedStrings[1] + "\n";
      fos.write(stringToWrite.getBytes());

      // handle the actual data
      while (null != (s = fRead.readLine())) {
        separatedStrings = s.split(COMMUNITY_DELIMITER);
        stringToWrite = separatedStrings[0] + separatedStrings[1];
        int currentComms = findNumberOfCommunities(separatedStrings[1]);
        numCommasToAdd = 1;
        if (currentComms < maxComms)
          numCommasToAdd = maxComms - currentComms + 1;
        for (int i = 0; i < numCommasToAdd; i++)
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
    }
    catch (IOException e) {
    }
  }

  private int findNumberOfCommunities(String comms) {
    int numCommas = 0;
    for (int i = 0; i < comms.length(); i++) {
      if (comms.charAt(i) == ',')
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

  private void deleteFileIfNotNull(File fileToDelete) {
    if (fileToDelete != null)
      fileToDelete.delete();
  }

  /**
   * Class encapsulating details of current state of projects, activities, participations, and
   * participants.
   * 
   * @author Raj
   */
  private class DataHolder {
    List<ProjectHolder> pHolder_data;
  }

  class ProjectHolder {
    Project p;

    List<ActivityHolder> activityHolderList;
  }

  class ActivityHolder {
    Activities a;

    List<ParticipationHolder> participationHolderList;
  }

  class ParticipationHolder {
    Participation pa;

    List<Participant> participantList;
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
      case R.id.action_exportbt:
        use_email_not_bt = false;
        shareData();
        break;
      case R.id.action_exportdata:
        use_email_not_bt = true;
        shareData();
        break;
      case R.id.action_help:
        HelpDialog helpDialog = new HelpDialog();
        helpDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        helpDialog.show(getSupportFragmentManager(), "helpdialog");
        break;
      case R.id.action_framework:
        FrameworkInfoDialog frameworkInfoDialog = new FrameworkInfoDialog();
        frameworkInfoDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        frameworkInfoDialog.show(getSupportFragmentManager(), "frameworkinfodialog");
        break;
      case R.id.action_glossary:
        GlossaryDialog glossaryDialog = new GlossaryDialog();
        glossaryDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        glossaryDialog.show(getSupportFragmentManager(), "glossarydialog");
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  private void shareData() {
    if (!dataToExportFound) {
      Toast.makeText(getApplicationContext(),
              getResources().getString(R.string.noparticipationstoexport), Toast.LENGTH_SHORT)
              .show();
      return;
    }
    showDialog(PROGRESS_DIALOG);

    if (sendEmailTask == null || sendEmailTask.getStatus() == Status.FINISHED)
      sendEmailTask = new SendDataTask(this);
    sendEmailTask.execute();
  }

  /**
   * Callback for SendDataTask onPostExecute()
   */
  public void shareDataCallback() {
    if (progressDialog.isShowing())
      progressDialog.dismiss();

    if (use_email_not_bt)
      sendByEmail();
    else
      sendByBT();
  }

  private void sendByBT() {
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

    if (btAdapter == null) {
      Toast.makeText(getApplicationContext(),
              getResources().getString(R.string.nobluetoothsupport), Toast.LENGTH_SHORT).show();
      return;
    }

    final Intent sendBTIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    sendBTIntent.setType("*/*");

    ArrayList<Uri> uris = new ArrayList<Uri>();
    uris.add(Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/" + dataFileName));
    if (signaturesToExportFound) {
      uris.add(Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/"
              + participationFileName));
      uris.add(Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/"
              + signInReportsFileName));
    }
    sendBTIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

    sendBTIntent.setComponent(new ComponentName("com.android.bluetooth",
            "com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
    startActivityForResult(sendBTIntent, ParticipationSummaryActivity.SENDBT_REQUEST);
    overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
  }

  private void sendByEmail() {
    final Intent sendEmailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
    sendEmailIntent.setType("text/plain");
    String uriText = "mailto:" + Uri.encode("") + "?subject=" + Uri.encode("RealTrack Data Report")
            + "&body=" + Uri.encode("Please find your data attached with this email.");
    Uri uri = Uri.parse(uriText);
    sendEmailIntent.setData(uri);

    ArrayList<Uri> uris = new ArrayList<Uri>();
    uris.add(Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/" + dataFileName));
    if (signaturesToExportFound) {
      uris.add(Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/"
              + participationFileName));
      uris.add(Uri.parse("content://" + CachedFileContentProvider.AUTHORITY + "/"
              + signInReportsFileName));
    }
    sendEmailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

    // stupid workaround to get GMail to work with SEND_MULTIPLE
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
      sendEmailIntent.setType(null);
      final Intent restrictIntent = new Intent(Intent.ACTION_SENDTO);
      Uri data = Uri.parse("mailto:?to=some@email.com");
      restrictIntent.setData(data);
      sendEmailIntent.setSelector(restrictIntent);
    }

    startActivityForResult(Intent.createChooser(sendEmailIntent, "Send mail..."),
            ParticipationSummaryActivity.SENDEMAIL_REQUEST);
    overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == SENDEMAIL_REQUEST) {
      deleteTemporaryFiles();
    }
    else if (requestCode == SENDBT_REQUEST) {
      deleteTemporaryFiles();
    }
  }

  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    return sendEmailTask;
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case PROGRESS_DIALOG:
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating Reports...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    return super.onCreateDialog(id);
  }

}