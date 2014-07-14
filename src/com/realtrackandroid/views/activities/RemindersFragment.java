package com.realtrackandroid.views.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.reminders.RemindersDAO;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.reminders.Reminders;
import com.realtrackandroid.views.dialogs.PickTimeDialog;

public class RemindersFragment extends SherlockFragment {

  private int dayOfWeek;

  private EditText mondayTime, tuesdayTime, wednesdayTime, thursdayTime, fridayTime, saturdayTime,
          sundayTime;

  private CheckBox mondayCheckbox, tuesdayCheckbox, wednesdayCheckbox, thursdayCheckbox,
          fridayCheckbox, saturdayCheckbox, sundayCheckbox;

  private View v;

  private Reminders r;

  private ActivitiesFragmentInterface mActivity;

  private Activities a;

  public static final RemindersFragment newInstance(String title) {
    RemindersFragment f = new RemindersFragment();
    return f;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mActivity = (ActivitiesFragmentInterface) activity;
    }
    catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
              + " must implement ActivitiesFragmentInterface");
    }
    a = mActivity.getActivities();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    v = inflater.inflate(R.layout.activity_addactivities_fragment_reminders, container, false);
    return v;
  }

  @Override
  public void onResume() {
    super.onResume();

    mondayCheckbox = (CheckBox) v.findViewById(R.id.mondayCheckBox);
    mondayCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!mondayCheckbox.isChecked())
          mondayTime.setText("");
      }
    });

    tuesdayCheckbox = (CheckBox) v.findViewById(R.id.tuesdayCheckBox);
    tuesdayCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!tuesdayCheckbox.isChecked())
          tuesdayTime.setText("");
      }
    });

    wednesdayCheckbox = (CheckBox) v.findViewById(R.id.wednesdayCheckBox);
    wednesdayCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!wednesdayCheckbox.isChecked())
          wednesdayTime.setText("");
      }
    });

    thursdayCheckbox = (CheckBox) v.findViewById(R.id.thursdayCheckBox);
    thursdayCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!thursdayCheckbox.isChecked())
          thursdayTime.setText("");
      }
    });

    fridayCheckbox = (CheckBox) v.findViewById(R.id.fridayCheckBox);
    fridayCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!fridayCheckbox.isChecked())
          fridayTime.setText("");
      }
    });

    saturdayCheckbox = (CheckBox) v.findViewById(R.id.saturdayCheckBox);
    saturdayCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!saturdayCheckbox.isChecked())
          saturdayTime.setText("");
      }
    });

    sundayCheckbox = (CheckBox) v.findViewById(R.id.sundayCheckBox);
    sundayCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!sundayCheckbox.isChecked())
          sundayTime.setText("");
      }
    });

    // entering the reminder time
    mondayTime = (EditText) v.findViewById(R.id.mondayTime);
    mondayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very
                                               // first selection of the text field
    // not doing this means the first click simply focuses the text field
    mondayTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mondayCheckbox.isChecked()) {
          dayOfWeek = 1;
          Bundle bundle = new Bundle();
          bundle.putString("timetodisplay", mondayTime.getText().toString());
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getActivity().getSupportFragmentManager(), "timepicker");
      }
    });

    tuesdayTime = (EditText) v.findViewById(R.id.tuesdayTime);
    tuesdayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very
                                                // first selection of the text field
    // not doing this means the first click simply focuses the text field
    tuesdayTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (tuesdayCheckbox.isChecked()) {
          dayOfWeek = 2;
          Bundle bundle = new Bundle();
          bundle.putString("timetodisplay", tuesdayTime.getText().toString());
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getActivity().getSupportFragmentManager(), "timepicker");
      }
    });

    wednesdayTime = (EditText) v.findViewById(R.id.wednesdayTime);
    wednesdayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very
                                                  // first selection of the text field
    // not doing this means the first click simply focuses the text field
    wednesdayTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (wednesdayCheckbox.isChecked()) {
          dayOfWeek = 3;
          Bundle bundle = new Bundle();
          bundle.putString("timetodisplay", wednesdayTime.getText().toString());
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getActivity().getSupportFragmentManager(), "timepicker");
      }
    });

    thursdayTime = (EditText) v.findViewById(R.id.thursdayTime);
    thursdayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very
                                                 // first selection of the text field
    // not doing this means the first click simply focuses the text field
    thursdayTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (thursdayCheckbox.isChecked()) {
          dayOfWeek = 4;
          Bundle bundle = new Bundle();
          bundle.putString("timetodisplay", thursdayTime.getText().toString());
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getActivity().getSupportFragmentManager(), "timepicker");
      }
    });

    fridayTime = (EditText) v.findViewById(R.id.fridayTime);
    fridayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very
                                               // first selection of the text field
    // not doing this means the first click simply focuses the text field
    fridayTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (fridayCheckbox.isChecked()) {
          dayOfWeek = 5;
          Bundle bundle = new Bundle();
          bundle.putString("timetodisplay", fridayTime.getText().toString());
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getActivity().getSupportFragmentManager(), "timepicker");
      }
    });

    saturdayTime = (EditText) v.findViewById(R.id.saturdayTime);
    saturdayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very
                                                 // first selection of the text field
    // not doing this means the first click simply focuses the text field
    saturdayTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (saturdayCheckbox.isChecked()) {
          dayOfWeek = 6;
          Bundle bundle = new Bundle();
          bundle.putString("timetodisplay", saturdayTime.getText().toString());
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getActivity().getSupportFragmentManager(), "timepicker");
      }
    });

    sundayTime = (EditText) v.findViewById(R.id.sundayTime);
    sundayTime.setFocusableInTouchMode(false); // do this so the date picker opens up on the very
                                               // first selection of the text field
    // not doing this means the first click simply focuses the text field
    sundayTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (sundayCheckbox.isChecked()) {
          dayOfWeek = 7;
          Bundle bundle = new Bundle();
          bundle.putString("timetodisplay", sundayTime.getText().toString());
          showTimePickerDialog(bundle);
        }
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getActivity().getSupportFragmentManager(), "timepicker");
      }
    });

    if (a != null) {
      int id = a.getId();

      // populate the reminder checkboxes
      RemindersDAO rDao = new RemindersDAO(getActivity());
      List<Reminders> reminders_data = rDao.getAllRemindersForActivityId(id);
      for (Reminders r : reminders_data) {
        DateFormat parser = new SimpleDateFormat("hh:mm aaa");
        Date d = new Date(r.getRemindTime());
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(r.getRemindTime());
        switch (c.get(Calendar.DAY_OF_WEEK)) {
          case Calendar.MONDAY:
            mondayCheckbox.setChecked(true);
            mondayTime.setText(parser.format(d));
            mondayTime.setTag(r.getId()); // will be used to update the reminder
            break;
          case Calendar.TUESDAY:
            tuesdayCheckbox.setChecked(true);
            tuesdayTime.setText(parser.format(d));
            tuesdayTime.setTag(r.getId()); // will be used to update the reminder
            break;
          case Calendar.WEDNESDAY:
            wednesdayCheckbox.setChecked(true);
            wednesdayTime.setText(parser.format(d));
            wednesdayTime.setTag(r.getId()); // will be used to update the reminder
            break;
          case Calendar.THURSDAY:
            thursdayCheckbox.setChecked(true);
            thursdayTime.setText(parser.format(d));
            thursdayTime.setTag(r.getId()); // will be used to update the reminder
            break;
          case Calendar.FRIDAY:
            fridayCheckbox.setChecked(true);
            fridayTime.setText(parser.format(d));
            fridayTime.setTag(r.getId()); // will be used to update the reminder
            break;
          case Calendar.SATURDAY:
            saturdayCheckbox.setChecked(true);
            saturdayTime.setText(parser.format(d));
            saturdayTime.setTag(r.getId()); // will be used to update the reminder
            break;
          case Calendar.SUNDAY:
            sundayCheckbox.setChecked(true);
            sundayTime.setText(parser.format(d));
            sundayTime.setTag(r.getId()); // will be used to update the reminder
            break;
        }
      }
    }
  }

  public void setTime(String time) {
    switch (dayOfWeek) {
      case 1:
        mondayTime.setText(time); // sets the chosen date in the text view
        break;
      case 2:
        tuesdayTime.setText(time); // sets the chosen date in the text view
        break;
      case 3:
        wednesdayTime.setText(time); // sets the chosen date in the text view
        break;
      case 4:
        thursdayTime.setText(time); // sets the chosen date in the text view
        break;
      case 5:
        fridayTime.setText(time); // sets the chosen date in the text view
        break;
      case 6:
        saturdayTime.setText(time); // sets the chosen date in the text view
        break;
      case 7:
        sundayTime.setText(time); // sets the chosen date in the text view
        break;
    }
  }

  public void setFields(Activities a, int newOrExistingActivityId) {
    if (v == null)
      return;

    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");

    // save reminders for this activity to the reminders table
    RemindersDAO rDao = new RemindersDAO(getActivity());
    parser = new SimpleDateFormat("hh:mm aaa");

    if (mondayCheckbox.isChecked()) {
      if (mondayTime.getText() != null) {
        try {
          Date date = parser.parse(mondayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the
          // hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us
          // (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only
          // extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
          r = new Reminders();
          r.setActivityid(newOrExistingActivityId);
          r.setRemindTime(c.getTimeInMillis());
          if (mondayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) mondayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getActivity());
          }
          else { // add a new reminder
            rDao.addReminders(r, getActivity());
          }
        }
        catch (ParseException e) {
        }
      }
    }
    else { // box was unchecked, remove any associated reminder for this day
      if (mondayTime.getTag() != null) {
        int reminderid = (Integer) mondayTime.getTag();
        rDao.deleteReminders(reminderid, getActivity());
        EditActivitiesActivity.deleteAlarmsForReminder(getActivity(), reminderid);
      }
    }

    if (tuesdayCheckbox.isChecked()) {
      if (tuesdayTime.getText() != null) {
        try {
          Date date = parser.parse(tuesdayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the
          // hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us
          // (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only
          // extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
          r = new Reminders();
          r.setActivityid(newOrExistingActivityId);
          r.setRemindTime(c.getTimeInMillis());
          if (tuesdayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) tuesdayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getActivity());
          }
          else { // add a new reminder
            rDao.addReminders(r, getActivity());
          }
        }
        catch (ParseException e) {
        }
      }
    }
    else { // box was unchecked, remove any associated reminder for this day
      if (tuesdayTime.getTag() != null) {
        int reminderid = (Integer) tuesdayTime.getTag();
        rDao.deleteReminders(reminderid, getActivity());
        EditActivitiesActivity.deleteAlarmsForReminder(getActivity(), reminderid);
      }
    }

    if (wednesdayCheckbox.isChecked()) {
      if (wednesdayTime.getText() != null) {
        try {
          Date date = parser.parse(wednesdayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the
          // hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us
          // (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only
          // extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
          r = new Reminders();
          r.setActivityid(newOrExistingActivityId);
          r.setRemindTime(c.getTimeInMillis());
          if (wednesdayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) wednesdayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getActivity());
          }
          else { // add a new reminder
            rDao.addReminders(r, getActivity());
          }
        }
        catch (ParseException e) {
        }
      }
    }
    else { // box was unchecked, remove any associated reminder for this day
      if (wednesdayTime.getTag() != null) {
        int reminderid = (Integer) wednesdayTime.getTag();
        rDao.deleteReminders(reminderid, getActivity());
        EditActivitiesActivity.deleteAlarmsForReminder(getActivity(), reminderid);
      }
    }

    if (thursdayCheckbox.isChecked()) {
      if (thursdayTime.getText() != null) {
        try {
          Date date = parser.parse(thursdayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the
          // hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us
          // (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only
          // extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
          r = new Reminders();
          r.setActivityid(newOrExistingActivityId);
          r.setRemindTime(c.getTimeInMillis());
          if (thursdayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) thursdayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getActivity());
          }
          else { // add a new reminder
            rDao.addReminders(r, getActivity());
          }
        }
        catch (ParseException e) {
        }
      }
    }
    else { // box was unchecked, remove any associated reminder for this day
      if (thursdayTime.getTag() != null) {
        int reminderid = (Integer) thursdayTime.getTag();
        rDao.deleteReminders(reminderid, getActivity());
        EditActivitiesActivity.deleteAlarmsForReminder(getActivity(), reminderid);
      }
    }

    if (fridayCheckbox.isChecked()) {
      if (fridayTime.getText() != null) {
        try {
          Date date = parser.parse(fridayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the
          // hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us
          // (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only
          // extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
          r = new Reminders();
          r.setActivityid(newOrExistingActivityId);
          r.setRemindTime(c.getTimeInMillis());
          if (fridayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) fridayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getActivity());
          }
          else { // add a new reminder
            rDao.addReminders(r, getActivity());
          }
        }
        catch (ParseException e) {
        }
      }
    }
    else { // box was unchecked, remove any associated reminder for this day
      if (fridayTime.getTag() != null) {
        int reminderid = (Integer) fridayTime.getTag();
        rDao.deleteReminders(reminderid, getActivity());
        EditActivitiesActivity.deleteAlarmsForReminder(getActivity(), reminderid);
      }
    }

    if (saturdayCheckbox.isChecked()) {
      if (saturdayTime.getText() != null) {
        try {
          Date date = parser.parse(saturdayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the
          // hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us
          // (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only
          // extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
          r = new Reminders();
          r.setActivityid(newOrExistingActivityId);
          r.setRemindTime(c.getTimeInMillis());
          if (saturdayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) saturdayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getActivity());
          }
          else { // add a new reminder
            rDao.addReminders(r, getActivity());
          }
        }
        catch (ParseException e) {
        }
      }
    }
    else { // box was unchecked, remove any associated reminder for this day
      if (saturdayTime.getTag() != null) {
        int reminderid = (Integer) saturdayTime.getTag();
        rDao.deleteReminders(reminderid, getActivity());
        EditActivitiesActivity.deleteAlarmsForReminder(getActivity(), reminderid);
      }
    }

    if (sundayCheckbox.isChecked()) {
      if (sundayTime.getText() != null) {
        try {
          Date date = parser.parse(sundayTime.getText().toString());
          // the date object we just constructed has only two fields that are of interest to us: the
          // hour and the
          // minute of the day at which the alarm should be set. The other fields are junk for us
          // (they are initialized
          // to some 1970 date. Hence, in the Calendar object that we construct below, we only
          // extract the hour and
          // minute from the date object.
          Calendar c = Calendar.getInstance();
          c.set(Calendar.HOUR_OF_DAY, date.getHours());
          c.set(Calendar.MINUTE, date.getMinutes());
          c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
          r = new Reminders();
          r.setActivityid(newOrExistingActivityId);
          r.setRemindTime(c.getTimeInMillis());
          if (sundayTime.getTag() != null) { // updating an existing reminder
            r.setId((Integer) sundayTime.getTag()); // retrieve the id of this reminder
            rDao.updateReminders(r, getActivity());
          }
          else { // add a new reminder
            rDao.addReminders(r, getActivity());
          }
        }
        catch (ParseException e) {
        }
      }
    }
    else { // box was unchecked, remove any associated reminder for this day
      if (sundayTime.getTag() != null) {
        int reminderid = (Integer) sundayTime.getTag();
        rDao.deleteReminders(reminderid, getActivity());
        EditActivitiesActivity.deleteAlarmsForReminder(getActivity(), reminderid);
      }
    }
  }

}
