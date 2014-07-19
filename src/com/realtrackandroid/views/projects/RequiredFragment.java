package com.realtrackandroid.views.projects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import com.realtrackandroid.models.projects.Project;
import com.realtrackandroid.views.dialogs.PickDateDialog;

public class RequiredFragment extends SherlockFragment {

  public static final RequiredFragment newInstance(String title) {
    RequiredFragment f = new RequiredFragment();
    return f;
  }

  private boolean startOrEnd;

  private EditText startDate;

  private EditText endDate;

  private View v;

  private EditText title;

  private ProjectFragmentInterface mActivity;

  private Project p;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mActivity = (ProjectFragmentInterface) activity;
    }
    catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement ProjectFragmentInterface");
    }

    p = mActivity.getProject();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    v = inflater.inflate(R.layout.activity_addproject_fragment_required, container, false);
    return v;
  }

  public boolean setFields(Project p) {
    if (v == null)
      return false;

    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    try {
      Date date = parser.parse(startDate.getText().toString());
      p.setStartDate(date.getTime());
      date = parser.parse(endDate.getText().toString());
      Calendar endCal = Calendar.getInstance();
      endCal.setTimeInMillis(date.getTime());
      endCal.set(Calendar.HOUR_OF_DAY, 23);
      endCal.set(Calendar.MINUTE, 59);
      p.setEndDate(endCal.getTimeInMillis());
    }
    catch (ParseException e) {
      Toast.makeText(getActivity(), R.string.fillrequiredfieldserrormessage, Toast.LENGTH_SHORT)
              .show();
      return false;
    }

    p.setTitle(title.getText().toString());
    if (p.getTitle().equals("")) {
      Toast.makeText(getActivity(), R.string.fillrequiredfieldserrormessage, Toast.LENGTH_SHORT)
              .show();
      return false;
    }

    return true;
  }

  public void setDate(String date) {
    if (startOrEnd)
      startDate.setText(date); // sets the chosen date in the text view
    else
      endDate.setText(date); // sets the chosen date in the text view
  }

  @Override
  public void onResume() {
    super.onResume();

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
        try {
          Date date = parser.parse(startDate.getText().toString());
          bundle.putLong("displaydate", date.getTime()); // really only required in
                                                         // EditProjectActivity (which is a subclass
                                                         // of this one) for editing a project
        }
        catch (ParseException e) {
        }
        try {
          Date date = parser.parse(endDate.getText().toString());
          bundle.putLong("maxdate", date.getTime());
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
        try {
          Date date = parser.parse(endDate.getText().toString());
          bundle.putLong("displaydate", date.getTime()); // really only required in
                                                         // EditProjectActivity (which is a subclass
                                                         // of this one) for editing a project
        }
        catch (ParseException e) {
        }
        try {
          Date date = parser.parse(startDate.getText().toString());
          bundle.putLong("mindate", date.getTime());
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

    if (p != null) {
      title.setText(p.getTitle());

      final DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
      startDate.setText(parser.format(p.getStartDate()));
      endDate.setText(parser.format(p.getEndDate()));
    }
  }

  public EditText getStartDate() {
    return startDate;
  }

  public EditText getEndDate() {
    return endDate;
  }

  public EditText getTitle() {
    return title;
  }
}
