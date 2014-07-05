package com.realtrackandroid.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.realtrackandroid.R;
import com.realtrackandroid.models.activities.Activities;

public class OptionalFragment extends SherlockFragment {
  
  private EditText notes, cohort, orgs, comms;
  
  public static final OptionalFragment newInstance(String title)
  {
    OptionalFragment f = new OptionalFragment();
    return f;
  }

  private View v;
  private ActivitiesFragmentInterface mActivity;
  private Activities a;
  private CheckBox malariaCheckBox;
  private CheckBox ecpaCheckBox;
  private CheckBox foodsecurityCheckBox;
  private CheckBox gendereqCheckBox;
  private CheckBox hivaidsCheckBox;
  private CompoundButton technologyfordevelopmentCheckBox;
  private CheckBox youthasresourcesCheckBox;
  private CheckBox volunteerismCheckBox;
  private CheckBox peoplewithdisabilitiesCheckBox;
  
  @Override
  public void onAttach(Activity activity) {
      super.onAttach(activity);
      try {
        mActivity = (ActivitiesFragmentInterface) activity;
      } catch (ClassCastException e) {
          throw new ClassCastException(activity.toString() + " must implement ActivitiesFragmentInterface");
      }
      a = mActivity.getActivities();
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      v = inflater.inflate(R.layout.activity_addactivities_fragment_optional, container, false);
      return v;
  }
  
  @Override
  public void onResume(){
    super.onResume();
    
    notes = (EditText) v.findViewById(R.id.notes);
    cohort = (EditText) v.findViewById(R.id.cohort);
    orgs = (EditText) v.findViewById(R.id.orgs);
    comms = (EditText) v.findViewById(R.id.comms);
    malariaCheckBox = (CheckBox) v.findViewById(R.id.malariaCheckBox);
    ecpaCheckBox = (CheckBox) v.findViewById(R.id.ECPACheckBox);
    foodsecurityCheckBox = (CheckBox) v.findViewById(R.id.foodSecurityCheckBox);
    gendereqCheckBox = (CheckBox) v.findViewById(R.id.gendereqCheckBox);
    hivaidsCheckBox = (CheckBox) v.findViewById(R.id.hivaidsCheckBox);
    technologyfordevelopmentCheckBox = (CheckBox) v.findViewById(R.id.technologyfordevelopmentCheckBox);
    youthasresourcesCheckBox = (CheckBox) v.findViewById(R.id.youthasresourcesCheckBox);
    volunteerismCheckBox = (CheckBox) v.findViewById(R.id.volunteerismCheckBox);
    peoplewithdisabilitiesCheckBox = (CheckBox) v.findViewById(R.id.peoplewithdisabilitiesCheckBox);
    
    if(a!=null){
      notes.setText(a.getNotes());
      cohort.setText(a.getCohort());
      orgs.setText(a.getOrgs());
      comms.setText(a.getComms());
      
      String[] initiativesList = a.getInitiatives().split("\\|");
      for (int i = 0; i < initiativesList.length; i++) {
        if (initiativesList[i].equals("1")) {
          switch (i) {
            case 0:
              malariaCheckBox.setChecked(true);
              break;
            case 1:
              ecpaCheckBox.setChecked(true);
              break;
            case 2:
              foodsecurityCheckBox.setChecked(true);
              break;
          }
        }
      }

      // populate the cspp checkboxes
      String[] csppList = a.getCspp().split("\\|");
      for (int i = 0; i < csppList.length; i++) {
        if (csppList[i].equals("1")) {
          switch (i) {
            case 0:
              gendereqCheckBox.setChecked(true);
              break;
            case 1:
              hivaidsCheckBox.setChecked(true);
              break;
            case 2:
              technologyfordevelopmentCheckBox.setChecked(true);
              break;
            case 3:
              youthasresourcesCheckBox.setChecked(true);
              break;
            case 4:
              volunteerismCheckBox.setChecked(true);
              break;
            case 5:
              peoplewithdisabilitiesCheckBox.setChecked(true);
              break;
          }
        }
      }
    }
  }
  
  public void setFields(Activities a){
    if(v==null)
      return;
    
    a.setNotes(notes.getText().toString());
    a.setCohort(cohort.getText().toString());
    a.setOrgs(orgs.getText().toString());
    a.setComms(comms.getText().toString());
    
    // store initiatives in compact form "x|x|x" where the first x is WID, second is Youth etc
    // this order MUST match the DisplayActivitiesActivity.AllInits array
    // If x == 1, this activity has the corresponding initiative, if 0 then it doesn't.
    String initiatives = (malariaCheckBox.isChecked() ? "1" : "0") + "|" +
            (ecpaCheckBox.isChecked() ? "1" : "0") + "|" +
            (foodsecurityCheckBox.isChecked() ? "1" : "0");
    a.setInitiatives(initiatives);

    // store cspp in compact form "x|x|x"
    // If x == 1, this activity has the corresponding cspp, if 0 then it doesn't.
    String cspp = (gendereqCheckBox.isChecked() ? "1" : "0") + "|" +
            (hivaidsCheckBox.isChecked() ? "1" : "0") + "|" +
            (technologyfordevelopmentCheckBox.isChecked() ? "1" : "0") + "|" +
            (youthasresourcesCheckBox.isChecked() ? "1" : "0") + "|" +
            (volunteerismCheckBox.isChecked() ? "1" : "0") + "|" +
            (peoplewithdisabilitiesCheckBox.isChecked() ? "1" : "0");
    a.setCspp(cspp);
    
    return;
  }
  
}
