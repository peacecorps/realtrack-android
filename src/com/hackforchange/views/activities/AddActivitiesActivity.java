package com.hackforchange.views.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.models.activities.Activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Add a new activity to an existing project
 */
// TODO Make sure required text fields are not empty
public class AddActivitiesActivity extends Activity {
  static final int DATE_DIALOG = 0;
  protected int mYear, mMonth, mDay;
  protected EditText title;
  protected EditText startDate;
  protected EditText endDate;
  protected EditText notes;
  protected EditText orgs;
  protected EditText comms;
  protected Button submitButton;
  protected boolean startOrEnd; // used in OnDateSetListener to distinguish between start date and end date field
  protected String initiatives;
  private int projectid;
  // used because we reuse the same listener for both fields
  protected Activities a;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.addactivitiesactivity);

    // get the owner project
    projectid = getIntent().getExtras().getInt("projectid");
  }

  @Override
  public void onResume(){
    super.onResume();
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // entering the start date
    startDate = (EditText) findViewById(R.id.startDate);
    startDate.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first selection of the text field
    // not doing this means the first click simply focuses the text field
    startDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startOrEnd = true;
        showDialog(DATE_DIALOG);
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
        showDialog(DATE_DIALOG);
      }
    });

    title = (EditText) findViewById(R.id.title);
    notes = (EditText) findViewById(R.id.notes);
    orgs = (EditText) findViewById(R.id.orgs);
    comms = (EditText) findViewById(R.id.comms);

    // store initiatives in compact form "xxxxx" where the first x is WID, second is Youth etc
    // If x == 1, this activity has the corresponding initiative, if 0 then it doesn't.
    initiatives = (((CheckBox) findViewById(R.id.widCheckBox)).isChecked()?"1":"0")+"|"+
                  (((CheckBox) findViewById(R.id.youthCheckBox)).isChecked()?"1":"0")+"|"+
                  (((CheckBox) findViewById(R.id.malariaCheckBox)).isChecked()?"1":"0")+"|"+
                  (((CheckBox) findViewById(R.id.ECPACheckBox)).isChecked()?"1":"0")+"|"+
                  (((CheckBox) findViewById(R.id.foodSecurityCheckBox)).isChecked()?"1":"0");

    submitButton = (Button) findViewById(R.id.submitbutton);
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        a = new Activities();
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        try {
          Date date = parser.parse(startDate.getText().toString());
          a.setStartDate(date.getTime());
          date = parser.parse(endDate.getText().toString());
          a.setEndDate(date.getTime());
        } catch (ParseException e) {
        }
        a.setTitle(title.getText().toString());
        a.setNotes(notes.getText().toString());
        a.setOrgs(orgs.getText().toString());
        a.setComms(comms.getText().toString());
        a.setInitiatives(initiatives);
        a.setProjectid(projectid);

        ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
        aDao.addActivities(a);
        finish();
      }
    });
  }

  // the callback received when the user "sets" the date in the dialog
  protected DatePickerDialog.OnDateSetListener mDateSetListener =
    new DatePickerDialog.OnDateSetListener() {
      public void onDateSet(DatePicker view, int year,
                            int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        if(startOrEnd)
          startDate.setText(String.format("%02d/%02d/%4d",(mMonth + 1),mDay,mYear)); //sets the chosen date in the text view
        else
          endDate.setText(String.format("%02d/%02d/%4d",(mMonth + 1),mDay,mYear)); //sets the chosen date in the text view
        removeDialog(DATE_DIALOG); // remember to remove the dialog or onCreateDialog will NOT be called again! We need it to be called afresh
        // each time either startDate or endDate is clicked because we prepopulate the date picker with different
        // dates for startDate and endDate in EditProjectActivity.java's overriden onCreateDialog
        // http://stackoverflow.com/questions/2222648/change-the-contents-of-an-android-dialog-box-after-creation
      }
    };

  @Override
  protected Dialog onCreateDialog(int id){
    switch(id){
      case DATE_DIALOG:
        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
    }
    return null;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case android.R.id.home:
        // provide a back button on the actionbar
        finish();
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }
}