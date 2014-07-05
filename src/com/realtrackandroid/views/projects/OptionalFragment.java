package com.realtrackandroid.views.projects;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.realtrackandroid.R;
import com.realtrackandroid.models.projects.Project;

public class OptionalFragment extends SherlockFragment {
  
  private View v;
  private EditText notes;
  private ProjectFragmentInterface mActivity;
  private Project p;
  
  public static final OptionalFragment newInstance(String title)
  {
    OptionalFragment f = new OptionalFragment();
    return f;
  }
  
  @Override
  public void onAttach(Activity activity) {
      super.onAttach(activity);
      try {
        mActivity = (ProjectFragmentInterface) activity;
      } catch (ClassCastException e) {
          throw new ClassCastException(activity.toString() + " must implement ProjectFragmentMarkerInterface");
      }
      p = mActivity.getProject();
  }
  
  public boolean setFields(Project p){
    if(v==null)
      return false;
    
    p.setNotes(notes.getText().toString());
    return true;
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      v = inflater.inflate(R.layout.activity_addproject_fragment_optional, container, false);
      return v;
  }
  
  @Override
  public void onResume(){
    super.onResume();
    
    notes = (EditText) v.findViewById(R.id.notes);
    
    if (p != null){
      notes.setText(p.getNotes());
    }
    
  }

  public EditText getNotes() {
    return notes;
  }
}
