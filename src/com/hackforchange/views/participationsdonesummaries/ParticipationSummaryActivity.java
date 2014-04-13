package com.hackforchange.views.participationsdonesummaries;

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

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Bundle;
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

public class ParticipationSummaryActivity extends SherlockActivity {
  static final int SENDEMAIL_REQUEST = 1;
  private static final Font TITLE_FONT = new Font(FontFamily.HELVETICA, 18);
  private static final int PROGRESS_DIALOG = 2;

  private LinearLayout projectSummaryLinearLayout;

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
  
  private XYSeries mCurrentSeries;
  private XYMultipleSeriesDataset mDataset;
  private GraphicalView mChartView;
  private XYMultipleSeriesRenderer mRenderer;

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

  private void testGraph(LinearLayout view) {
    mDataset = new XYMultipleSeriesDataset();
    mCurrentSeries = new XYSeries("");
    mDataset.addSeries(mCurrentSeries);
    mRenderer = new XYMultipleSeriesRenderer();
    XYSeriesRenderer renderer = new XYSeriesRenderer();
    renderer.setFillPoints(false);
    renderer.setDisplayChartValues(false);
    renderer.setColor(getResources().getColor(R.color.blue));
    
    FillOutsideLine fill = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ALL);
    fill.setColor(getResources().getColor(R.color.blue));
    renderer.addFillOutsideLine(fill);
    
    mRenderer.setClickEnabled(false);
    mRenderer.setShowLegend(false);
    mRenderer.setAntialiasing(true);
    mRenderer.setAxisTitleTextSize(20f);
    mRenderer.setXLabelsColor(getResources().getColor(R.color.darkgrey));
    mRenderer.setYLabelsColor(0, getResources().getColor(R.color.darkgrey));
    mRenderer.setLabelsTextSize(15f);
    mRenderer.setXLabelsPadding(2f);
    mRenderer.setYLabelsPadding(10f);
    mRenderer.setXTitle("Weeks");
    mRenderer.setYTitle("Participants");
    mRenderer.setYLabels(3);
    mRenderer.setMargins(new int[]{5, 45, 5, 5});
    
    mRenderer.setInScroll(true);
    
    mRenderer.setMarginsColor(getResources().getColor(R.color.white));
    
    mRenderer.addSeriesRenderer(renderer);
    
    mRenderer.setZoomEnabled(false);
    mRenderer.setPanEnabled(false);
    
    mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
    LinearLayout graphView = (LinearLayout) view.findViewById(R.id.activityGraph);
    graphView.addView(mChartView);
    mCurrentSeries.add(1, 2);
    mCurrentSeries.add(2, 3);
    mCurrentSeries.add(3, 0.5);
    mCurrentSeries.add(5, 2.5);
    mCurrentSeries.add(6, 3.5);
    mCurrentSeries.add(7, 2.85);
    mCurrentSeries.add(8, 3.25);
    mCurrentSeries.add(9, 4.25);
    mCurrentSeries.add(10, 3.75);
    mRenderer.setRange(new double[] { 1, 10.5, 0, 4.75 });
    mChartView.repaint();
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
          paHolder.pa = pa;
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
    projectSummaryLinearLayout = (LinearLayout) findViewById(R.id.projectsummarylayout);
    projectSummaryLinearLayout.removeAllViews();

    for (ProjectHolder pHolder : dHolder.pHolder_data) {
      Project p = pHolder.p;

      TextView projectTitle = (TextView) getLayoutInflater().inflate(R.layout.row_projectsummary, null);;
      projectTitle.setText(p.getTitle());

      boolean projectTitleAdded = false;

      for (ActivityHolder aHolder : pHolder.activityHolderList) {
        Activities a = aHolder.a;
        LinearLayout activitySummaryView = (LinearLayout) getLayoutInflater().inflate(R.layout.row_activitysummary, null);

        if(!aHolder.participationHolderList.isEmpty())
          dataToExportFound = true;

        int sumParticipants = 0;
        XYMultipleSeriesRenderer renderer = null;
        XYMultipleSeriesDataset dataset = null;

        for (ParticipationHolder paHolder : aHolder.participationHolderList) {
          Participation participation = paHolder.pa;
          sumParticipants += participation.getTotalParticipants();
        }

        if (aHolder.participationHolderList.size() > 0) {
          if(!projectTitleAdded){
            projectSummaryLinearLayout.addView(projectTitle);
            projectTitleAdded = true;
          }

          TextView activityTitle = (TextView) activitySummaryView.findViewById(R.id.activityTitle);
          activityTitle.setText(a.getTitle());
          TextView numEvents = (TextView) activitySummaryView.findViewById(R.id.numEvents);
          numEvents.setText("["+aHolder.participationHolderList.size()+"] event(s)");
          TextView totalParticipants = (TextView) activitySummaryView.findViewById(R.id.totalParticipants);
          totalParticipants.setText("["+sumParticipants+"] participants");
          
          testGraph(activitySummaryView);
          
          projectSummaryLinearLayout.addView(activitySummaryView);
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
      signinDocument = new Document(PageSize.A4);
      PdfWriter.getInstance(signinDocument, signinFos);
      signinDocument.open();
      signinDocument.addTitle("RealTrack Sign-In Report");
      Paragraph reportHeader = new Paragraph("RealTrack Sign-In Report", TITLE_FONT); 
      reportHeader.add(new Paragraph("Report generated on: "+(new SimpleDateFormat("MM/dd/yyyy hh:mm aaa").format(new Date()))));
      signinDocument.add(reportHeader);
      LineSeparator ls = new LineSeparator();
      signinDocument.add(new Chunk(ls));
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
          Participation participation = paHolder.pa;

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
            projectParagraph.add(new Paragraph("Activity Title: " + a.getTitle()));
            projectParagraph.add(new Paragraph("Sign-In Sheet for: " + dateParser.format(d)+" " + timeParser.format(d)));
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
    Participation pa;
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