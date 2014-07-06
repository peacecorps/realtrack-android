package com.realtrackandroid.views.participationsactive;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.realtrackandroid.R;
import com.realtrackandroid.models.activities.Participation;

public class OptionalFragmentRecordOrEditParticipation extends SherlockFragment {
  protected EditText spmen09NumText, spmen1017NumText, spmen1824NumText, spmenOver25NumText,
  spwomen09NumText, spwomen1017NumText, spwomen1824NumText, spwomenOver25NumText, notesText;

  protected CheckBox spmen09Checkbox, spmen1017Checkbox, spmen1824Checkbox, spmenOver25Checkbox,
  spwomen09Checkbox, spwomen1017Checkbox, spwomen1824Checkbox, spwomenOver25Checkbox;

  public static final OptionalFragmentRecordOrEditParticipation newInstance(String title)
  {
    OptionalFragmentRecordOrEditParticipation f = new OptionalFragmentRecordOrEditParticipation();
    return f;
  }

  private View v;
  private RecordOrEditParticipationFragmentInterface mActivity;
  private Participation p;
  private boolean editParticipation;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mActivity = (RecordOrEditParticipationFragmentInterface) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement RecordParticipationFragmentMarkerInterface");
    }
    p = mActivity.getParticipation();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
    v = inflater.inflate(R.layout.activity_recordoreditparticipation_fragment_optional, container, false);
    return v;
  }

  @Override
  public void onResume(){
    super.onResume();

    spmen09Checkbox = (CheckBox) v.findViewById(R.id.spmen09CheckBox);
    spmen1017Checkbox = (CheckBox) v.findViewById(R.id.spmen1017CheckBox);
    spmen1824Checkbox = (CheckBox) v.findViewById(R.id.spmen1824CheckBox);
    spmenOver25Checkbox = (CheckBox) v.findViewById(R.id.spmenOver25CheckBox);
    spwomen09Checkbox = (CheckBox) v.findViewById(R.id.spwomen09CheckBox);
    spwomen1017Checkbox = (CheckBox) v.findViewById(R.id.spwomen1017CheckBox);
    spwomen1824Checkbox = (CheckBox) v.findViewById(R.id.spwomen1824CheckBox);
    spwomenOver25Checkbox = (CheckBox) v.findViewById(R.id.spwomenOver25CheckBox);
    spmen09NumText = (EditText) v.findViewById(R.id.numSpMen09);
    spmen1017NumText = (EditText) v.findViewById(R.id.numSpMen1017);
    spmen1824NumText = (EditText) v.findViewById(R.id.numSpMen1824);
    spmenOver25NumText = (EditText) v.findViewById(R.id.numSpMenOver25);
    spwomen09NumText = (EditText) v.findViewById(R.id.numSpWomen09);
    spwomen1017NumText = (EditText) v.findViewById(R.id.numSpWomen1017);
    spwomen1824NumText = (EditText) v.findViewById(R.id.numSpWomen1824);
    spwomenOver25NumText = (EditText) v.findViewById(R.id.numSpWomenOver25);

    notesText = (EditText) v.findViewById(R.id.notes);

    if(editParticipation){
      notesText.setText(p.getNotes());
      updateServiceProviderCounts();
    }
    
    CheckBox[] checkBoxArray = {spmen09Checkbox, spmen1017Checkbox, spmen1824Checkbox, spmenOver25Checkbox,
            spwomen09Checkbox, spwomen1017Checkbox, spwomen1824Checkbox, spwomenOver25Checkbox};

    EditText[] numTextArray = {spmen09NumText, spmen1017NumText, spmen1824NumText, spmenOver25NumText,
            spwomen09NumText, spwomen1017NumText, spwomen1824NumText, spwomenOver25NumText};

    for (int i=0; i<checkBoxArray.length; ++i){
      final CheckBox c = checkBoxArray[i];
      final EditText e = numTextArray[i];
      e.setEnabled(false);
      c.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!c.isChecked()){
            e.setEnabled(false);
            e.setText("");
          }
          else{
            e.setEnabled(true);
          }
        }
      });
    }
  }
  
  private void updateServiceProviderCounts() {
    if(p.getSpMen09()>0){
      spmen09NumText.setText(Integer.toString(p.getSpMen09()));
      spmen09Checkbox.setChecked(true);
    }
    if(p.getSpMen1017()>0){
      spmen1017NumText.setText(Integer.toString(p.getSpMen1017()));
      spmen1017Checkbox.setChecked(true);
    }
    if(p.getSpMen1824()>0){
      spmen1824NumText.setText(Integer.toString(p.getSpMen1824()));
      spmen1824Checkbox.setChecked(true);
    }
    if(p.getSpMenOver25()>0){
      spmenOver25NumText.setText(Integer.toString(p.getSpMenOver25()));
      spmenOver25Checkbox.setChecked(true);
    }
    if(p.getSpWomen09()>0){
      spwomen09NumText.setText(Integer.toString(p.getSpWomen09()));
      spwomen09Checkbox.setChecked(true);
    }
    if(p.getSpWomen1017()>0){
      spwomen1017NumText.setText(Integer.toString(p.getSpWomen1017()));
      spwomen1017Checkbox.setChecked(true);
    }
    if(p.getSpWomen1824()>0){
      spwomen1824NumText.setText(Integer.toString(p.getSpWomen1824()));
      spwomen1824Checkbox.setChecked(true);
    }
    if(p.getSpWomenOver25()>0){
      spwomenOver25NumText.setText(Integer.toString(p.getSpWomenOver25()));
      spwomenOver25Checkbox.setChecked(true);
    }
  }
  
  public void setFields(Participation p){
    if (spmen09Checkbox.isChecked()) {
      if (spmen09NumText.getText().length() == 0){
        Toast.makeText(getActivity(), R.string.emptyparticipationmessage,
                Toast.LENGTH_SHORT).show();
        return;
      }
      else{
        p.setSpMen09(Integer.parseInt(spmen09NumText.getText().toString()));
      }
    }
    else {
      p.setSpMen09(0);
    }
    
    if (spmen1017Checkbox.isChecked()) {
      if (spmen1017NumText.getText().length() == 0){
        Toast.makeText(getActivity(), R.string.emptyparticipationmessage,
                Toast.LENGTH_SHORT).show();
        return;
      }
      else{
        p.setSpMen1017(Integer.parseInt(spmen1017NumText.getText().toString()));
      }
    }
    else {
      p.setSpMen1017(0);
    }
    
    if (spmen1824Checkbox.isChecked()) {
      if (spmen1824NumText.getText().length() == 0){
        Toast.makeText(getActivity(), R.string.emptyparticipationmessage,
                Toast.LENGTH_SHORT).show();
        return;
      }
      else{
        p.setSpMen1824(Integer.parseInt(spmen1824NumText.getText().toString()));
      }
    }
    else {
      p.setSpMen1824(0);
    }
    
    if (spmenOver25Checkbox.isChecked()) {
      if (spmenOver25NumText.getText().length() == 0){
        Toast.makeText(getActivity(), R.string.emptyparticipationmessage,
                Toast.LENGTH_SHORT).show();
        return;
      }
      else{
        p.setSpMenOver25(Integer.parseInt(spmenOver25NumText.getText().toString()));
      }
    }
    else {
      p.setSpMenOver25(0);
    }
    
    if (spwomen09Checkbox.isChecked()) {
      if (spwomen09NumText.getText().length() == 0){
        Toast.makeText(getActivity(), R.string.emptyparticipationmessage,
                Toast.LENGTH_SHORT).show();
        return;
      }
      else{
        p.setSpWomen09(Integer.parseInt(spwomen09NumText.getText().toString()));
      }
    }
    else {
      p.setSpWomen09(0);
    }
    
    if (spwomen1017Checkbox.isChecked()) {
      if (spwomen1017NumText.getText().length() == 0){
        Toast.makeText(getActivity(), R.string.emptyparticipationmessage,
                Toast.LENGTH_SHORT).show();
        return;
      }
      else{
        p.setSpWomen1017(Integer.parseInt(spwomen1017NumText.getText().toString()));
      }
    }
    else {
      p.setSpWomen1017(0);
    }
    
    if (spwomen1824Checkbox.isChecked()) {
      if (spwomen1824NumText.getText().length() == 0){
        Toast.makeText(getActivity(), R.string.emptyparticipationmessage,
                Toast.LENGTH_SHORT).show();
        return;
      }
      else{
        p.setSpWomen1824(Integer.parseInt(spwomen1824NumText.getText().toString()));
      }
    }
    else {
      p.setSpWomen1824(0);
    }
    
    if (spwomenOver25Checkbox.isChecked()) {
      if (spwomenOver25NumText.getText().length() == 0){
        Toast.makeText(getActivity(), R.string.emptyparticipationmessage,
                Toast.LENGTH_SHORT).show();
        return;
      }
      else{
        p.setSpWomenOver25(Integer.parseInt(spwomenOver25NumText.getText().toString()));
      }
    }
    else {
      p.setSpWomenOver25(0);
    }
    
    p.setNotes(notesText.getText().toString());
  }

  public boolean isEditParticipation() {
    return editParticipation;
  }

  public void setEditParticipation(boolean editParticipation) {
    this.editParticipation = editParticipation;
  }
}
