package com.realtrackandroid.views.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ActivitiesDAO;
import com.realtrackandroid.backend.reminders.RemindersDAO;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.reminders.Reminders;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.HelpDialog;

/*
 * Presents an activity that displays details of an existing activity
 * Also lets you edit the project (EditActivitiesActivity) or delete the project (right from this java file)
 * by choosing buttons in the ActionBar
 * Pressing the back key will exit the activity
 */
public class DisplayActivitiesActivity extends SherlockFragmentActivity {
  public String[] allInits, allCspps;
  private int activitiesid;
  private Activities a;
  private ArrayList<Reminders> reminders_data;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_displayactivities);

    // read in the ID of the project that this activity must display details of
    activitiesid = getIntent().getExtras().getInt("activitiesid");
  }

  @Override
  public void onResume() {
    super.onResume();
    allInits = updateInitiativeNames();
    allCspps = updateCsppNames();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
    a = aDao.getActivityWithId(activitiesid);
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(a.getTitle());
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    Date d = new Date(a.getStartDate());
    TextView startDate = (TextView) findViewById(R.id.startDate);
    startDate.setText(parser.format(d));
    d = new Date(a.getEndDate());
    TextView endDate = (TextView) findViewById(R.id.endDate);
    endDate.setText(parser.format(d));
    TextView notes = (TextView) findViewById(R.id.notes);
    if(a.getNotes().length()>0)
      notes.setText("Notes:\n"+a.getNotes());
    TextView cohort = (TextView) findViewById(R.id.cohort);
    if(a.getCohort().length()>0)
      cohort.setText("Cohort:\n"+a.getCohort());
    TextView orgs = (TextView) findViewById(R.id.orgs);
    if(a.getOrgs().length()>0)
      orgs.setText("Organizations involved:\n"+a.getOrgs());
    TextView comms = (TextView) findViewById(R.id.comms);
    if(a.getComms().length()>0)
      comms.setText("Communities affected:\n"+a.getComms());
    
    TextView initiatives = (TextView) findViewById(R.id.initiatives);
    
    // convert initiatives back to human-readable form
    String[] initiativesList = a.getInitiatives().split("\\|");
    String inits = "";
    for (int i = 0; i < initiativesList.length; i++) {
      if (initiativesList[i].equals("1"))
        inits += allInits[i] + "\n";
    }
    inits = (inits.length() > 1) ? inits.substring(0, inits.length() - 1) : ""; // remove the last superfluous newline character
    if(inits.length()>0)
      initiatives.setText("Initiatives:\n"+inits);
    
    TextView csppTextView = (TextView) findViewById(R.id.cspp);

    // convert initiatives back to human-readable form
    String[] csppList = a.getCspp().split("\\|");
    String cspp = "";
    for (int i = 0; i < csppList.length; i++) {
      if (csppList[i].equals("1"))
        cspp += allCspps[i] + "\n";
    }
    cspp = (cspp.length() > 1) ? cspp.substring(0, cspp.length() - 1) : ""; // remove the last superfluous newline character
    if(cspp.length()>0)
      csppTextView.setText("Cross-sector programming priorities:\n"+cspp);

    // display reminders
    TextView reminders = (TextView) findViewById(R.id.reminders);
    RemindersDAO rDao = new RemindersDAO(getApplicationContext());
    reminders_data = rDao.getAllRemindersForActivityId(activitiesid);
    String remindersText = "";
    DateFormat dayParser = new SimpleDateFormat("EEEE");
    DateFormat timeParser = new SimpleDateFormat("hh:mm aaa");
    for (Reminders r : reminders_data) {
      remindersText += dayParser.format(r.getRemindTime()) + "s at " + timeParser.format(r.getRemindTime()) + "\n";
    }
    remindersText = (remindersText.length() > 1) ? remindersText.substring(0, remindersText.length() - 1) : ""; // remove the last superfluous newline character
    if(remindersText.length()>0)
      reminders.setText("Reminders:\n"+remindersText);
  }
  
  private String[] updateCsppNames() {
    return new String[]{getResources().getString(R.string.genderequalityandwomensempowerment),
            getResources().getString(R.string.hivaids),
            getResources().getString(R.string.technologyfordevelopment),
            getResources().getString(R.string.youthasresources),
            getResources().getString(R.string.volunteerism),
            getResources().getString(R.string.peoplewithdisabilities)};
  }

  private String[] updateInitiativeNames() {
    return new String[]{getResources().getString(R.string.malaria), getResources().getString(R.string.ecpa),
            getResources().getString(R.string.foodsecurity)};
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.displayactivitiesmenu, menu);
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
      case R.id.action_deleteactivity:
        // warn the user first!
        new AlertDialog.Builder(this)
        .setMessage("Are you sure you want to delete this activity? This CANNOT be undone.")
        .setCancelable(false)
        .setNegativeButton("No", null)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
            int activityId = DisplayActivitiesActivity.this.activitiesid;
            aDao.deleteActivities(activityId);
            // cancel all alarms for participation events of the reminders of this activity
            RemindersDAO rDao = new RemindersDAO(getApplicationContext());
            ArrayList<Reminders> reminders_data;
            reminders_data = rDao.getAllRemindersForActivityId(activityId);
            for (Reminders r : reminders_data) {
              EditActivitiesActivity.deleteAlarmsForReminder(getApplicationContext(), r.getId());
            }
            finish();
          }
        })
        .show();
        break;
      case R.id.action_editactivity:
        Intent i = new Intent(DisplayActivitiesActivity.this, EditActivitiesActivity.class);
        i.putExtra("activitiesid", activitiesid);
        i.putExtra("projectid", a.getProjectid());
        startActivity(i);
        overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
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
        HelpDialog glossaryDialog = new HelpDialog();
        glossaryDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        glossaryDialog.setDisplayUrl("file:///android_asset/glossary.html");
        glossaryDialog.show(getSupportFragmentManager(), "glossarydialog");
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }
}