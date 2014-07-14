package com.realtrackandroid.views.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.realtrackandroid.R;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.projects.Project;
import com.realtrackandroid.views.dialogs.PickDateDialog;

public class RequiredFragment extends SherlockFragment {

  private EditText title, startDate, endDate;

  private boolean startOrEnd;

  private long projectStartDate, projectEndDate;

  private Activities a;

  public static final RequiredFragment newInstance(String title) {
    RequiredFragment f = new RequiredFragment();
    return f;
  }

  private View v;

  private Project p;

  private ActivitiesFragmentInterface mActivity;

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
    p = mActivity.getProject();
    a = mActivity.getActivities();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    v = inflater.inflate(R.layout.activity_addactivities_fragment_required, container, false);
    return v;
  }

  @Override
  public void onResume() {
    super.onResume();
    projectStartDate = p.getStartDate();
    projectEndDate = p.getEndDate();

    // entering the start date
    startDate = (EditText) v.findViewById(R.id.startDate);
    startDate.setFocusableInTouchMode(false); // do this so the date picker opens up on the very
                                              // first selection of the text field
    // not doing this means the first click simply focuses the text field
    startDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startOrEnd = true;
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Bundle bundle = new Bundle();
        bundle.putLong("mindate", projectStartDate);
        try {
          Date date = parser.parse(endDate.getText().toString());
          Long maxDate = date.getTime() < projectEndDate ? date.getTime() : projectEndDate; // choose
                                                                                            // the
                                                                                            // lesser
                                                                                            // of
                                                                                            // the
                                                                                            // two
                                                                                            // for
                                                                                            // the
                                                                                            // upper
                                                                                            // bound
          bundle.putLong("maxdate", maxDate);
        }
        catch (ParseException e) {
          bundle.putLong("maxdate", projectEndDate);
        }
        try {
          Date date = parser.parse(startDate.getText().toString());
          bundle.putLong("displaydate", date.getTime()); // really only required in
                                                         // EditActivitiesActivity (which is a
                                                         // subclass of this one) for editing an
                                                         // activity
        }
        catch (ParseException e) {
        }
        showDatePickerDialog(bundle);
      }

      private void showDatePickerDialog(Bundle bundle) {
        PickDateDialog pickDateDialog = new PickDateDialog();
        pickDateDialog.setArguments(bundle);
        pickDateDialog.show(getActivity().getSupportFragmentManager(), "datepicker");
      }
    });

    // entering the end date
    endDate = (EditText) v.findViewById(R.id.endDate);
    endDate.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first
                                            // selection of the text field
    // not doing this means the first click simply focuses the text field
    endDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startOrEnd = false;
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Bundle bundle = new Bundle();
        bundle.putLong("maxdate", projectEndDate);
        try {
          Date date = parser.parse(startDate.getText().toString());
          Long minDate = date.getTime() > projectStartDate ? date.getTime() : projectStartDate; // choose
                                                                                                // the
                                                                                                // larger
                                                                                                // of
                                                                                                // the
                                                                                                // two
                                                                                                // for
                                                                                                // the
                                                                                                // lower
                                                                                                // bound
          bundle.putLong("mindate", minDate);
        }
        catch (ParseException e) {
          bundle.putLong("mindate", projectStartDate);
        }
        try {
          Date date = parser.parse(endDate.getText().toString());
          bundle.putLong("displaydate", date.getTime()); // really only required in
                                                         // EditActivitiesActivity (which is a
                                                         // subclass of this one) for editing an
                                                         // activity
        }
        catch (ParseException e) {
        }
        showDatePickerDialog(bundle);
      }

      private void showDatePickerDialog(Bundle bundle) {
        PickDateDialog pickDateDialog = new PickDateDialog();
        pickDateDialog.setArguments(bundle);
        pickDateDialog.show(getActivity().getSupportFragmentManager(), "datepicker");
      }
    });

    title = (EditText) v.findViewById(R.id.title);

    if (a != null) {
      title.setText(a.getTitle());
      DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
      Date d = new Date(a.getStartDate());
      startDate.setText(parser.format(d));
      d = new Date(a.getEndDate());
      endDate.setText(parser.format(d));
    }
  }

  public void setDate(String date) {
    if (startOrEnd)
      startDate.setText(date); // sets the chosen date in the text view
    else
      endDate.setText(date); // sets the chosen date in the text view
  }

  public boolean setFields(Activities a) {
    if (v == null)
      return false;

    // save the start and end date
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    try {
      Date date = parser.parse(startDate.getText().toString());
      a.setStartDate(date.getTime());
      date = parser.parse(endDate.getText().toString());
      date.setHours(23);
      date.setMinutes(59);
      a.setEndDate(date.getTime());
    }
    catch (ParseException e) {
      Toast.makeText(getActivity(), R.string.fillrequiredfieldserrormessage, Toast.LENGTH_SHORT)
              .show();
      return false;
    }

    // save title and other params
    a.setTitle(title.getText().toString());
    if (a.getTitle().equals("")) {
      Toast.makeText(getActivity(), R.string.fillrequiredfieldserrormessage, Toast.LENGTH_SHORT)
              .show();
      return false;
    }

    return true;
  }
}
