package com.realtrackandroid.views.projects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.ActionBar;
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
public class AddProjectActivity extends SherlockFragmentActivity implements PickDateDialogListener, TabListener, ProjectFragmentMarkerInterface {
  protected int mYear, mMonth, mDay;
  protected EditText title;
  protected EditText startDate;
  protected EditText endDate;
  protected EditText notes;
  protected StyledButton submitButton;
  protected boolean startOrEnd; // used to distinguish between start date and end date field
  protected Project p;
  private OptionalFragment optionalFragment;
  private RequiredFragment requiredFragment;
  private Tab requiredTab, optionalTab;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.base_fragment);
    
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    
    requiredFragment = new RequiredFragment();
    optionalFragment = new OptionalFragment();
    
    requiredTab = getSupportActionBar().newTab().setText(R.string.required);
    requiredTab.setTabListener(this);
    optionalTab = getSupportActionBar().newTab().setText(R.string.optional);
    optionalTab.setTabListener(this);
    
    getSupportActionBar().addTab(requiredTab);
    getSupportActionBar().addTab(optionalTab);
    
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(R.id.fragment_container, requiredFragment);
    ft.add(R.id.fragment_container, optionalFragment);
    ft.commit();
  }

  @Override
  public void onResume() {
    super.onResume();

    // entering the start date
    startDate = (EditText) requiredFragment.getView().findViewById(R.id.startDate);
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
    endDate = (EditText) requiredFragment.getView().findViewById(R.id.endDate);
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

    title = (EditText) requiredFragment.getView().findViewById(R.id.title);
    notes = (EditText) optionalFragment.getView().findViewById(R.id.notes);
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
      case R.id.action_glossary:
        HelpDialog glossaryDialog = new HelpDialog();
        glossaryDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        glossaryDialog.setDisplayUrl("file:///android_asset/glossary.html");
        glossaryDialog.show(getSupportFragmentManager(), "glossarydialog");
        break;
      case R.id.action_save:
        saveProject();
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  private void saveProject() {
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

  @Override
  public void onTabSelected(Tab tab, FragmentTransaction ft) {
    switch(tab.getPosition()){
      case 0:
        ft.show(requiredFragment);
        ft.hide(optionalFragment);
        break;
      case 1:
        ft.hide(requiredFragment);
        ft.show(optionalFragment);
        break;
    }
  }

  @Override
  public void onTabUnselected(Tab tab, FragmentTransaction ft) {
  }

  @Override
  public void onTabReselected(Tab tab, FragmentTransaction ft) {
  }

}