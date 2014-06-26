package com.realtrackandroid.views.projects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.projects.ProjectDAO;
import com.realtrackandroid.common.StyledButton;
import com.realtrackandroid.models.projects.Project;
import com.realtrackandroid.views.dialogs.PickDateDialog;
import com.realtrackandroid.views.dialogs.PickDateDialogListener;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.HelpDialog;

/*
 * Presents an activity that lets you add a new project
 * Pressing the back key will exit the activity without adding a project
 */
// TODO: Make sure required text fields are not empty
// TODO: make sure activity dates don't go out of project dates
// TODO: make sure repeating alarms stop when the activity ends
public class AddProjectActivity extends SherlockFragmentActivity implements PickDateDialogListener {
  protected int mYear, mMonth, mDay;
  protected EditText title;
  protected EditText startDate;
  protected EditText endDate;
  protected EditText notes;
  protected StyledButton submitButton;
  protected boolean startOrEnd; // used to distinguish between start date and end date field
  protected Project p;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_addproject);
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // entering the start date
    startDate = (EditText) findViewById(R.id.startDate);
    startDate.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
    // not doing this means the first click simply focuses the text field
    startDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startOrEnd = true;
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Bundle bundle = new Bundle();
        try {
          Date date = parser.parse(startDate.getText().toString());
          bundle.putLong("displaydate", date.getTime()); // really only required in EditProjectActivity (which is a subclass of this one) for editing a project
        } catch (ParseException e) {
        }
        try {
          Date date = parser.parse(endDate.getText().toString());
          bundle.putLong("maxdate", date.getTime());
        } catch (ParseException e) {
        }
        showDatePickerDialog(bundle);
      }

      private void showDatePickerDialog(Bundle bundle) {
        PickDateDialog pickDateDialog = new PickDateDialog();
        pickDateDialog.setArguments(bundle);
        pickDateDialog.show(getSupportFragmentManager(), "datepicker");
      }
    });

    // entering the end date
    endDate = (EditText) findViewById(R.id.endDate);
    endDate.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
    // not doing this means the first click simply focuses the text field
    endDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startOrEnd = false;
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Bundle bundle = new Bundle();
        try {
          Date date = parser.parse(endDate.getText().toString());
          bundle.putLong("displaydate", date.getTime()); // really only required in EditProjectActivity (which is a subclass of this one) for editing a project
        } catch (ParseException e) {
        }
        try {
          Date date = parser.parse(startDate.getText().toString());
          bundle.putLong("mindate", date.getTime());
        } catch (ParseException e) {
        }
        showDatePickerDialog(bundle);
      }

      private void showDatePickerDialog(Bundle bundle) {
        PickDateDialog pickDateDialog = new PickDateDialog();
        pickDateDialog.setArguments(bundle);
        pickDateDialog.show(getSupportFragmentManager(), "datepicker");
      }
    });

    title = (EditText) findViewById(R.id.title);
    notes = (EditText) findViewById(R.id.notes);

    submitButton = (StyledButton) findViewById(R.id.submitbutton);
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        p = new Project();
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
        pDao.addProject(p);
        finish();
      }
    });
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.addprojectmenu, menu);

    getSupportActionBar().setDisplayShowTitleEnabled(true);
    return true;
  }

  @Override
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
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  @Override
  public void setDate(String date) {
    if (startOrEnd)
      startDate.setText(date); //sets the chosen date in the text view
    else
      endDate.setText(date); //sets the chosen date in the text view
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }

}