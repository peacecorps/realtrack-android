package com.realtrackandroid.views.participationsactive;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ActivitiesDAO;
import com.realtrackandroid.backend.activities.ParticipantDAO;
import com.realtrackandroid.common.StyledButton;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.activities.Participant;
import com.realtrackandroid.models.activities.Participation;
import com.realtrackandroid.views.participationsactive.signinsheet.SignInSheetLandingActivity;

public class RequiredFragmentRecordOrEditParticipation extends SherlockFragment {
  static final int ADD_PARTICIPANTS_REQUEST = 1;

  private EditText men09NumText, men1017NumText, men1824NumText, menOver25NumText, women09NumText,
          women1017NumText, women1824NumText, womenOver25NumText;

  private int men09ManuallyEntered, men1017ManuallyEntered, men1824ManuallyEntered,
          menOver25ManuallyEntered, women09ManuallyEntered, women1017ManuallyEntered,
          women1824ManuallyEntered, womenOver25ManuallyEntered;

  private int men09FromSignInSheet, men1017FromSignInSheet, men1824FromSignInSheet,
          menOver25FromSignInSheet, women09FromSignInSheet, women1017FromSignInSheet,
          women1824FromSignInSheet, womenOver25FromSignInSheet;

  private StyledButton signinSheetButton;

  private long dateTime;

  private ArrayList<Participant> participantList;

  public static final RequiredFragmentRecordOrEditParticipation newInstance(String title) {
    RequiredFragmentRecordOrEditParticipation f = new RequiredFragmentRecordOrEditParticipation();
    return f;
  }

  private RecordOrEditParticipationFragmentInterface mActivity;

  private View v;

  private Participation p;

  private boolean editParticipation;

  private boolean errorsFound;

  private ParticipantDAO participantDao;

  private ActivitiesDAO aDao;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mActivity = (RecordOrEditParticipationFragmentInterface) activity;
    }
    catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
              + " must implement RecordParticipationFragmentMarkerInterface");
    }
    p = mActivity.getParticipation();
    if (!editParticipation)
      participantList = new ArrayList<Participant>();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    v = inflater.inflate(R.layout.activity_recordoreditparticipation_fragment_required, container,
            false);
    return v;
  }

  @Override
  public void onResume() {
    super.onResume();

    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(dateTime);

    participantDao = new ParticipantDAO(getActivity());
    aDao = new ActivitiesDAO(getActivity());
    final Activities a = aDao.getActivityWithId(p.getActivityid());
    DateFormat simpleDateParser = new SimpleDateFormat("MM/dd/yyyy");
    final String participationDate = simpleDateParser.format(cal.getTime());

    // display title for this activity
    TextView title = (TextView) v.findViewById(R.id.title);
    title.setText(new ActivitiesDAO(getActivity()).getActivityWithId(p.getActivityid()).getTitle());

    // display date and time for this reminder
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy, EEEE, hh:mm aaa"); // example: 07/04/2013,
                                                                             // Thursday, 6:13 PM
    TextView datetime = (TextView) v.findViewById(R.id.datetime);
    datetime.setText(parser.format(cal.getTime()));

    // opening the sign-in sheet
    signinSheetButton = (StyledButton) v.findViewById(R.id.openSigninSheetButton);
    signinSheetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(getActivity(), SignInSheetLandingActivity.class);
        i.putExtra("activitytitle", a.getTitle()); // displayed on SignInSheetActivity
        i.putExtra("participationdate", participationDate); // displayed on SignInSheetActivity
        Bundle resultBundle = new Bundle();
        resultBundle.putParcelableArrayList("participantList", participantList);
        i.putExtras(resultBundle);
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

    if (editParticipation) {
      participantList = participantDao.getAllParticipantsForParticipationId(p.getId());
    }

    updateParticipantCounts();

  }

  private void updateParticipantCounts() {
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

    if (editParticipation) {
      men09ManuallyEntered = p.getMen09() - men09FromSignInSheet;
      men1017ManuallyEntered = p.getMen1017() - men1017FromSignInSheet;
      men1824ManuallyEntered = p.getMen1824() - men1824FromSignInSheet;
      menOver25ManuallyEntered = p.getMenOver25() - menOver25FromSignInSheet;
      women09ManuallyEntered = p.getWomen09() - women09FromSignInSheet;
      women1017ManuallyEntered = p.getWomen1017() - women1017FromSignInSheet;
      women1824ManuallyEntered = p.getWomen1824() - women1824FromSignInSheet;
      womenOver25ManuallyEntered = p.getWomenOver25() - womenOver25FromSignInSheet;
      editParticipation = false; // makes sure we only do the above once
    }

    // Note that even though we reinitialize menUnder15, men1524
    // etc to 0 in this method, there is no way their values can be less than what they were the
    // last time around
    // there is no way that a participant once submitted via the sign-in sheet can be removed i.e.,
    // these values
    // are strictly increasing.

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalMen09 = men09FromSignInSheet + men09ManuallyEntered;
    if (totalMen09 > 0) {
      men09NumText.setText(Integer.toString(totalMen09));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalMen1017 = men1017FromSignInSheet + men1017ManuallyEntered;
    if (totalMen1017 > 0) {
      men1017NumText.setText(Integer.toString(totalMen1017));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalMen1824 = men1824FromSignInSheet + men1824ManuallyEntered;
    if (totalMen1824 > 0) {
      men1824NumText.setText(Integer.toString(totalMen1824));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalMenOver25 = menOver25FromSignInSheet + menOver25ManuallyEntered;
    if (totalMenOver25 > 0) {
      menOver25NumText.setText(Integer.toString(totalMenOver25));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalWomen09 = women09FromSignInSheet + women09ManuallyEntered;
    if (totalWomen09 > 0) {
      women09NumText.setText(Integer.toString(totalWomen09));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalWomen1017 = women1017FromSignInSheet + women1017ManuallyEntered;
    if (totalWomen1017 > 0) {
      women1017NumText.setText(Integer.toString(totalWomen1017));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalWomen1824 = women1824FromSignInSheet + women1824ManuallyEntered;
    if (totalWomen1824 > 0) {
      women1824NumText.setText(Integer.toString(totalWomen1824));
    }

    // prevent the PCV from disabling this checkbox if at least one participant is in this category
    int totalWomenOver25 = womenOver25FromSignInSheet + womenOver25ManuallyEntered;
    if (totalWomenOver25 > 0) {
      womenOver25NumText.setText(Integer.toString(totalWomenOver25));
    }

    signinSheetButton.setText(getResources().getString(R.string.openSigninSheetButtonLabel) + " ("
            + participantList.size() + " participant(s))");
  }

  public boolean setFields(Participation p) {
    if (v == null)
      return false;

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

    for (int i = 0; i < participantList.size(); ++i) {
      Participant participant = participantList.get(i);
      if (participant.getId() != -1) { // the -1 indicates this is a participant not already in the
                                       // database
        participantList.remove(i--);
      }
      else {
        participant.setParticipationId(p.getId());
      }
    }

    // the -1 we set into the id won't affect the actual database write
    // because we ignore the id field of the participant object there
    participantDao.addParticipants(participantList);

    return true;
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

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == ADD_PARTICIPANTS_REQUEST) {
      if (resultCode == Activity.RESULT_OK) {
        Bundle resultBundle = intent.getExtras();
        participantList = resultBundle.getParcelableArrayList("participantList");
        updateParticipantCounts();
      }
    }
  }

  public boolean isEditParticipation() {
    return editParticipation;
  }

  public void setEditParticipation(boolean editParticipation) {
    this.editParticipation = editParticipation;
  }

  public long getDateTime() {
    return dateTime;
  }

  public void setDateTime(long dateTime) {
    this.dateTime = dateTime;
  }
}
