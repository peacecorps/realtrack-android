package com.realtrackandroid.views.projects;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.realtrackandroid.R;

public class OptionalFragment extends SherlockFragment {
  @Override
  public void onAttach(Activity activity) {
      super.onAttach(activity);
      try {
        ProjectFragmentMarkerInterface mActivity = (ProjectFragmentMarkerInterface) activity;
      } catch (ClassCastException e) {
          throw new ClassCastException(activity.toString() + " must implement ProjectFragmentMarkerInterface");
      }
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      return inflater.inflate(R.layout.activity_addproject_fragment_optional, container, false);
  }
}
