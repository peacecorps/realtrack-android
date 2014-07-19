package com.realtrackandroid.views.participationsactive;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ParticipantDAO;
import com.realtrackandroid.common.StyledButton;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.activities.Participant;
import com.realtrackandroid.models.activities.Participation;
import com.realtrackandroid.views.dialogs.PickDateDialog;
import com.realtrackandroid.views.dialogs.PickTimeDialog;
import com.realtrackandroid.views.participationsactive.signinsheet.SignInSheetLandingActivity;

public class RequiredFragmentRecordQuickParticipation extends SherlockFragment {
  static final int ADD_PARTICIPANTS_REQUEST = 1;

  private EditText men09NumText, men1017NumText, men1824NumText, menOver25NumText, women09NumText,
          women1017NumText, women1824NumText, womenOver25NumText;

  private int men09FromSignInSheet, men1017FromSignInSheet, men1824FromSignInSheet,
          menOver25FromSignInSheet, women09FromSignInSheet, women1017FromSignInSheet,
          women1824FromSignInSheet, womenOver25FromSignInSheet;

  private StyledButton signinSheetButton;

  private EditText date, time;

  private View v;

  private Activities a;

  private RecordQuickParticipationFragmentInterface mActivity;

  private ArrayList<Participant> participantList;

  private boolean errorsFound;

  public static final RequiredFragmentRecordQuickParticipation newInstance(String title) {
    RequiredFragmentRecordQuickParticipation f = new RequiredFragmentRecordQuickParticipation();
    return f;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mActivity = (RecordQuickParticipationFragmentInterface) activity;
    }
    catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
              + " must implement RecordQuickParticipationFragmentInterface");
    }
    a = mActivity.getActivities();
    participantList = new ArrayList<Participant>();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    v = inflater.inflate(R.layout.activity_recordquickparticipation_fragment_required, container,
            false);
    return v;
  }

  @Override
  public void onResume() {
    super.onResume();

    TextView title = (TextView) v.findViewById(R.id.title);
    title.setText(a.getTitle());

    date = (EditText) v.findViewById(R.id.date);
    date.setFocusableInTouchMode(false); // do this so the date picker opens up on the very first
    // selection of the text field
    // not doing this means the first click simply focuses the text field
    date.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putLong("mindate", a.getStartDate());
        bundle.putLong("maxdate", a.getEndDate());
        showDatePickerDialog(bundle);
      }

      private void showDatePickerDialog(Bundle bundle) {
        PickDateDialog pickDateDialog = new PickDateDialog();
        pickDateDialog.setArguments(bundle);
        pickDateDialog.show(getActivity().getSupportFragmentManager(), "datepicker");
      }
    });

    // entering the reminder time
    time = (EditText) v.findViewById(R.id.time);
    time.setFocusableInTouchMode(false); // do this so the time picker opens up on the very first
    // selection of the text field
    // not doing this means the first click simply focuses the text field
    time.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("timetodisplay", time.getText().toString());
        showTimePickerDialog(bundle);
      }

      private void showTimePickerDialog(Bundle bundle) {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(bundle);
        pickTimeDialog.show(getActivity().getSupportFragmentManager(), "timepicker");
      }
    });

    // opening the sign-in sheet
    signinSheetButton = (StyledButton) v.findViewById(R.id.openSigninSheetButton);
    signinSheetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(getActivity(), SignInSheetLandingActivity.class);
        i.putExtra("activitytitle", a.getTitle()); // displayed on SignInSheetActivity
        Bundle resultBundle = new Bundle();
        resultBundle.putParcelableArrayList("participantList", participantList);
        i.putExtras(resultBundle);
        if (date.getText().toString().length() != 0) {
          i.putExtra("participationdate", date.getText().toString()); // displayed on
                                                                      // SignInSheetActivity
        }
        i.putExtra("firstOpen", true); // used to jump straight to SignInSheetActivity the very
                                       // first time
        startActivityForResult(i, ADD_PARTICIPANTS_REQUEST);
        getActivity().overridePendingTransition(R.anim.animation_slideinright,
                R.anim.animation_slideoutleft);
      }
    });

    men09NumText = (EditText) v.findViewById(R.id.numMen09);
    men1017NumText = (EditText) v.findViewById(R.id.numMen1017);
    men1824NumText = (EditText) v.findViewById(R.id.numMen1824);
    menOver25NumText = (EditText) v.findViewById(R.id.numMenOver25);
    women09NumText = (EditText) v.findViewById(R.id.numWomen09);
    women1017NumText = (EditText) v.findViewById(R.id.numWomen1017);
    women1824NumText = (EditText) v.findViewById(R.id.numWomen1824);
    womenOver25NumText = (EditText) v.findViewById(R.id.numWomenOver25);
  }

  public void updateParticipantCountsFromSigninSheet() {
    if (participantList.isEmpty())
      return;

    men09FromSignInSheet = 0;
    men1017FromSignInSheet = 0;
    men1824FromSignInSheet = 0;
    menOver25FromSignInSheet = 0;
    women09FromSignInSheet = 0;
    women1017FromSignInSheet = 0;
    women1824FromSignInSheet = 0;
    womenOver25FromSignInSheet = 0;

    for (Participant p : participantList) {
      if (p.getGender() == Participant.MALE) {
        if (p.getAge() < 10)
          men09FromSignInSheet++;
        else if (p.getAge() < 18)
          men1017FromSignInSheet++;
        else if (p.getAge() < 25)
          men1824FromSignInSheet++;
        else
          menOver25FromSignInSheet++;
      }
      else if (p.getGender() == Participant.FEMALE) { // could simply be an else but just being
                                                      // cautious and making sure the value is
                                                      // FEMALE
        if (p.getAge() < 10)
          women09FromSignInSheet++;
        else if (p.getAge() < 18)
          women1017FromSignInSheet++;
        else if (p.getAge() < 25)
          women1824FromSignInSheet++;
        else
          womenOver25FromSignInSheet++;
      }
    }

    // set filters on the text fields so the PCV cannot manually enter a number less than the
    // current number of participants. Note that even though we reinitialize menUnder15, men1524
    // etc to 0 in this method, there is no way their values can be less than what they were because
    // there is no way that a participant once submitted via the sign-in sheet can be removed.

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if (men09FromSignInSheet > 0) {
      men09NumText.setText(Integer.toString(men09FromSignInSheet));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if (men1017FromSignInSheet > 0) {
      men1017NumText.setText(Integer.toString(men1017FromSignInSheet));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if (men1824FromSignInSheet > 0) {
      men1824NumText.setText(Integer.toString(men1824FromSignInSheet));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if (menOver25FromSignInSheet > 0) {
      menOver25NumText.setText(Integer.toString(menOver25FromSignInSheet));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if (women09FromSignInSheet > 0) {
      women09NumText.setText(Integer.toString(women09FromSignInSheet));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if (women1017FromSignInSheet > 0) {
      women1017NumText.setText(Integer.toString(women1017FromSignInSheet));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if (women1824FromSignInSheet > 0) {
      women1824NumText.setText(Integer.toString(women1824FromSignInSheet));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    if (womenOver25FromSignInSheet > 0) {
      womenOver25NumText.setText(Integer.toString(womenOver25FromSignInSheet));
    }

    signinSheetButton.setText(getResources().getString(R.string.openSigninSheetButtonLabel) + " ("
            + participantList.size() + " participant(s))");
  }

  public boolean setFields(Participation p) {
    if (v == null)
      return false;

    DateFormat dateParser = new SimpleDateFormat("MM/dd/yyyy"); // example: 07/04/2013
    DateFormat timeParser = new SimpleDateFormat("hh:mm aaa"); // example: 07/04/2013
    try {
      Calendar c = Calendar.getInstance();
      // set date
      c.setTimeInMillis((dateParser.parse(date.getText().toString())).getTime());
      // set time
      Date date = timeParser.parse(time.getText().toString());
      // the date object we just constructed has only two fields that are of interest to us: the
      // hour and the
      // minute of the day at which the alarm should be set. The other fields are junk for us
      // (they are initialized
      // to some 1970 date. Hence, in the Calendar object that we constructed, we only extract
      // the hour and
      // minute from the date object.
      Calendar c1 = Calendar.getInstance();
      c1.setTimeInMillis(date.getTime());
      c.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY));
      c.set(Calendar.MINUTE, c1.get(Calendar.MINUTE));
      p.setDate(c.getTimeInMillis());
    }
    catch (ParseException e) {
      Toast.makeText(getActivity(), R.string.fillrequiredfieldserrormessage, Toast.LENGTH_SHORT)
              .show();
      return false;
    }

    // set men, women and serviced
    if (men09NumText.getText().length() == 0 && men1017NumText.getText().length() == 0
            && men1824NumText.getText().length() == 0 && menOver25NumText.getText().length() == 0
            && women09NumText.getText().length() == 0 && women1017NumText.getText().length() == 0
            && women1824NumText.getText().length() == 0
            && womenOver25NumText.getText().length() == 0) {
      Toast.makeText(getActivity(), R.string.emptyparticipationmessage, Toast.LENGTH_SHORT).show();
      return false;
    }

    errorsFound = false;

    checkEnteredValueNotLessThanSigninSheetValue(men09NumText, men09FromSignInSheet);
    if (men09NumText.getText().length() != 0)
      p.setMen09(Integer.parseInt(men09NumText.getText().toString()));
    else
      p.setMen09(0);

    checkEnteredValueNotLessThanSigninSheetValue(men1017NumText, men1017FromSignInSheet);
    if (men1017NumText.getText().length() != 0)
      p.setMen1017(Integer.parseInt(men1017NumText.getText().toString()));
    else
      p.setMen1017(0);

    checkEnteredValueNotLessThanSigninSheetValue(men1824NumText, men1824FromSignInSheet);
    if (men1824NumText.getText().length() != 0)
      p.setMen1824(Integer.parseInt(men1824NumText.getText().toString()));
    else
      p.setMen1824(0);

    checkEnteredValueNotLessThanSigninSheetValue(menOver25NumText, menOver25FromSignInSheet);
    if (menOver25NumText.getText().length() != 0)
      p.setMenOver25(Integer.parseInt(menOver25NumText.getText().toString()));
    else
      p.setMenOver25(0);

    checkEnteredValueNotLessThanSigninSheetValue(women09NumText, women09FromSignInSheet);
    if (women09NumText.getText().length() != 0)
      p.setWomen09(Integer.parseInt(women09NumText.getText().toString()));
    else
      p.setWomen09(0);

    checkEnteredValueNotLessThanSigninSheetValue(women1017NumText, women1017FromSignInSheet);
    if (women1017NumText.getText().length() != 0)
      p.setWomen1017(Integer.parseInt(women1017NumText.getText().toString()));
    else
      p.setWomen1017(0);

    checkEnteredValueNotLessThanSigninSheetValue(women1824NumText, women1824FromSignInSheet);
    if (women1824NumText.getText().length() != 0)
      p.setWomen1824(Integer.parseInt(women1824NumText.getText().toString()));
    else
      p.setWomen1824(0);

    checkEnteredValueNotLessThanSigninSheetValue(womenOver25NumText, womenOver25FromSignInSheet);
    if (womenOver25NumText.getText().length() != 0)
      p.setWomenOver25(Integer.parseInt(womenOver25NumText.getText().toString()));
    else
      p.setWomenOver25(0);
    if (errorsFound) {
      Toast.makeText(getActivity(), R.string.cannotentersmallernumber, Toast.LENGTH_SHORT).show();
      return false;
    }

    return true;
  }

  public void updateParticipants(int newParticipationId) {
    ParticipantDAO participantDao = new ParticipantDAO(getActivity());
    // write the participant information
    // first add the participation id
    // the participation id is only assigned after the participation
    // has been added to its own table)
    for (Participant participant : participantList) {
      participant.setParticipationId(newParticipationId);
    }

    participantDao.addParticipants(participantList);
  }

  private void checkEnteredValueNotLessThanSigninSheetValue(EditText editText, int numSignedIn) {
    editText.setTextColor(getResources().getColor(android.R.color.black));
    if (editText.getText().length() == 0) {
      if (numSignedIn != 0)
        signalErrorInTextField(editText, numSignedIn);
      return;
    }

    int enteredValue = Integer.parseInt(editText.getText().toString());
    if (numSignedIn != 0 && enteredValue < numSignedIn) {
      signalErrorInTextField(editText, numSignedIn);
    }
  }

  private void signalErrorInTextField(EditText editText, int numSignedIn) {
    editText.setText(Integer.toString(numSignedIn)); // put back at least the number of people
                                                     // signed in

    // change the text color to signal an error so that the user can see it easily
    editText.setTextColor(getResources().getColor(R.color.orange));

    // restore the text color when the user tries to type in a possible correction
    final EditText fEditText = editText;
    editText.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        fEditText.setTextColor(getResources().getColor(android.R.color.black));
        return false;
      }
    });
    errorsFound = true;
  }

  public void setDate(String selectedDate) {
    date.setText(selectedDate);
  }

  public void setTime(String selectedTime) {
    time.setText(selectedTime);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == ADD_PARTICIPANTS_REQUEST) {
      if (resultCode == Activity.RESULT_OK) {
        Bundle resultBundle = intent.getExtras();
        participantList = resultBundle.getParcelableArrayList("participantList");

        updateParticipantCountsFromSigninSheet();
      }
    }
  }
}
