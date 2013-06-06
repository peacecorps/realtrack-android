package com.hackforchange.views.projects;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import com.hackforchange.backend.projects.ProjectDAO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
  public void onResume(){
    super.onResume();

    // pre-populate the fields with the project details from the DB
    // the user can then change them if he so desires (the changes are handled
    // from AddProjectActivity
    ProjectDAO pDao = new ProjectDAO(getApplicationContext());
    p = pDao.getProjectWithId(id);
    title.setText(p.getTitle());
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    Date d = new Date(p.getStartDate());
    startDate.setText(parser.format(d));
    d = new Date(p.getEndDate());
    endDate.setText(parser.format(d));
    notes.setText(p.getNotes());

    // change the submit button listener to UPDATE the existing project instead of creating a NEW one
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        try {
          Date date = parser.parse(startDate.getText().toString());
          p.setStartDate(date.getTime());
          date = parser.parse(endDate.getText().toString());
          p.setEndDate(date.getTime());
        } catch (ParseException e) {
        }
        p.setTitle(title.getText().toString());
        p.setNotes(notes.getText().toString());

        ProjectDAO pDao = new ProjectDAO(getApplicationContext());
        pDao.updateProject(p);
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