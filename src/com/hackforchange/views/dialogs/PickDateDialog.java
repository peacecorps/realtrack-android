package com.hackforchange.views.dialogs;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class PickDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
  
  PickDateDialogListener callingActivity;

  public PickDateDialog() {
  }
  
  @Override
  public void onAttach(Activity activity){
    super.onAttach(activity);
    try{
      callingActivity = (PickDateDialogListener) activity;
    } catch (ClassCastException e){
      throw(new ClassCastException("ERROR: " + activity.toString() + " MUST implement the PickDateDialogListener interface!!!"));
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Use the current date as the default date in the picker
    // get the start date
    final Calendar c = Calendar.getInstance();
    Long startDate = getArguments().getLong("startdate");
    Long endDate = getArguments().getLong("enddate");
    c.setTime(new Date(startDate));
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);

    // Create a new instance of DatePickerDialog and return it
    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
    datePickerDialog.getDatePicker().setMinDate(startDate);
    datePickerDialog.getDatePicker().setMaxDate(endDate);
    datePickerDialog.getDatePicker().setCalendarViewShown(false); //Unpredictable Android crap again. Workaround for CalendarView bug that causes NPE: http://stackoverflow.com/a/18700331
    return datePickerDialog;
  }

  @Override
  public void onDateSet(DatePicker view, int year, int month, int day) {
    callingActivity.setDate(String.format("%02d/%02d/%4d", (month + 1), day, year));
  }

}
