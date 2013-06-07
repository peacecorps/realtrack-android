package com.hackforchange.views.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.models.activities.Activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 * Presents an activity that lets you edit an EXISTING activities
 * Reuses most of the code as well as the layout of AddActivitiesActivity
 * Pressing the back key will exit the activity WITHOUT modding the activities
 */
public class EditActivitiesActivity extends AddActivitiesActivity{
  private int id;
  private int projectid; // used to update the existing activity

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // read in the ID of the activities that this activity must display details of
    id = getIntent().getExtras().getInt("activitiesid");
  }

  @Override
  public void onResume(){
    super.onResume();

    // pre-populate the fields with the activities details from the DB
    // the user can then change them if he so desires (the changes are handled
    // from AddActivitiesActivity
    ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
    a = aDao.getActivityWithId(id);
    title.setText(a.getTitle());
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    Date d = new Date(a.getStartDate());
    startDate.setText(parser.format(d));
    d = new Date(a.getEndDate());
    endDate.setText(parser.format(d));
    notes.setText(a.getNotes());
    orgs.setText(a.getOrgs());
    comms.setText(a.getComms());
    projectid = a.getProjectid();

    String[] initiativesList = a.getInitiatives().split("\\|");
    for(int i=0; i<initiativesList.length; i++){
      if(initiativesList[i].equals("1")){
        switch(i){
          case 0:
            ((CheckBox) findViewById(R.id.widCheckBox)).setChecked(true);
            break;
          case 1:
            ((CheckBox) findViewById(R.id.youthCheckBox)).setChecked(true);
            break;
          case 2:
            ((CheckBox) findViewById(R.id.malariaCheckBox)).setChecked(true);
            break;
          case 3:
            ((CheckBox) findViewById(R.id.ECPACheckBox)).setChecked(true);
            break;
          case 4:
            ((CheckBox) findViewById(R.id.foodSecurityCheckBox)).setChecked(true);
            break;
        }
      }
    }

    // change the submit button listener to UPDATE the existing activities instead of creating a NEW one
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

        // store initiatives in compact form "x|x|x|x|x" where the first x is WID, second is Youth etc
        // this order MUST match the DisplayActivitiesActivity.AllInits array
        // If x == 1, this activity has the corresponding initiative, if 0 then it doesn't.
        initiatives = (((CheckBox) findViewById(R.id.widCheckBox)).isChecked()?"1":"0")+"|"+
          (((CheckBox) findViewById(R.id.youthCheckBox)).isChecked()?"1":"0")+"|"+
          (((CheckBox) findViewById(R.id.malariaCheckBox)).isChecked()?"1":"0")+"|"+
          (((CheckBox) findViewById(R.id.ECPACheckBox)).isChecked()?"1":"0")+"|"+
          (((CheckBox) findViewById(R.id.foodSecurityCheckBox)).isChecked()?"1":"0");
        a.setInitiatives(initiatives);

        a.setProjectid(projectid);
        a.setId(id);

        ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
        aDao.updateActivities(a);
        finish();
      }
    });
  }

  @Override
  protected Dialog onCreateDialog(int id){
    switch(id){
      case DATE_DIALOG:
        // get the prepopulated date
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Date date;
        try {
          if(startOrEnd)
            date = parser.parse(startDate.getText().toString());
          else
            date = parser.parse(endDate.getText().toString());

          final Calendar c = Calendar.getInstance();
          c.setTime(date);
          mYear = c.get(Calendar.YEAR);
          mMonth = c.get(Calendar.MONTH);
          mDay = c.get(Calendar.DAY_OF_MONTH);
          return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        } catch (ParseException e) {
        }
    }
    return null;
  }
}