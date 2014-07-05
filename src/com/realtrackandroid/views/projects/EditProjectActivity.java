package com.realtrackandroid.views.projects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.projects.ProjectDAO;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.HelpDialog;

/*
 * Presents an activity that lets you edit an EXISTING project
 * Reuses most of the code as well as the layout of AddProjectActivity
 * Pressing the back key will exit the activity WITHOUT modding the project
 */
public class EditProjectActivity extends AddProjectActivity {
  private int id;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // read in the ID of the project that this activity must display details of
    id = getIntent().getExtras().getInt("projectid");
  }

  @Override
  public void onResume() {
    super.onResume();

    // pre-populate the fields with the project details from the DB
    // the user can then change them if he so desires (the changes are handled
    // from AddProjectActivity
    ProjectDAO pDao = new ProjectDAO(getApplicationContext());
    p = pDao.getProjectWithId(id);
    title.setText(p.getTitle());
    
    final DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    startDate.setText(parser.format(p.getStartDate()));
    endDate.setText(parser.format(p.getEndDate()));
    notes.setText(p.getNotes());
  }
  
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // provide a back button on the actionbar
        finish();
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
      case R.id.action_save:
        updateProject();
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }
  
  private void updateProject() {
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    try {
      Date date = parser.parse(startDate.getText().toString());
      p.setStartDate(date.getTime());
      date = parser.parse(endDate.getText().toString());
      date.setHours(23);
      date.setMinutes(59);
      p.setEndDate(date.getTime());
    } catch (ParseException e) {
      Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage, Toast.LENGTH_SHORT).show();
      return;
    }

    p.setTitle(title.getText().toString());
    if (p.getTitle().equals("")) {
      Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage, Toast.LENGTH_SHORT).show();
      return;
    }

    p.setNotes(notes.getText().toString());

    ProjectDAO pDao = new ProjectDAO(getApplicationContext());
    pDao.updateProject(p);
    finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }
}