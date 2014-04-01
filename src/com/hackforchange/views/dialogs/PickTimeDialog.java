package com.hackforchange.views.dialogs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

public class PickTimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

  PickTimeDialogListener callingActivity;

  public PickTimeDialog() {
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      callingActivity = (PickTimeDialogListener) activity;
    }
    catch (ClassCastException e) {
      throw (new ClassCastException("ERROR: " + activity.toString() + " MUST implement the PickTimeDialogListener interface!!!"));
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // get the prepopulated date
    DateFormat parser = new SimpleDateFormat("hh:mm aaa");
    Date date;
    Calendar c = Calendar.getInstance();
    String timeToDisplay = getArguments().getString("timetodisplay");
    try {
      date = parser.parse(timeToDisplay);
      c.setTime(date);
    }
    catch (ParseException e) {
    }
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);
    return new TimePickerDialog(getActivity(), this, hour, minute, false);
  }

  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    String timeToDisplay = String.format("%02d:%02d %s", ((hourOfDay / 12) > 0 ? ((hourOfDay == 12) ? 12 : (hourOfDay - 12))
                    : ((hourOfDay == 0) ? 12 : hourOfDay)), minute, ((hourOfDay / 12) > 0 ? "PM"
                    : "AM"));
    callingActivity.setTime(timeToDisplay);
  }

}
