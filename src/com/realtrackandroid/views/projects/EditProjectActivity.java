package com.realtrackandroid.views.projects;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.projects.ProjectDAO;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.GlossaryDialog;
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

    id = getIntent().getExtras().getInt("projectid");
    ProjectDAO pDao = new ProjectDAO(getApplicationContext());
    p = pDao.getProjectWithId(id);
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
        GlossaryDialog glossaryDialog = new GlossaryDialog();
        glossaryDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
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
    if (!requiredFragment.setFields(p))
      return;

    optionalFragment.setFields(p);

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