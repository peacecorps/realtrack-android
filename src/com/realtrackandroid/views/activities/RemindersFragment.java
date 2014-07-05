package com.realtrackandroid.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.realtrackandroid.R;

public class RemindersFragment extends SherlockFragment {
  @Override
  public void onAttach(Activity activity) {
      super.onAttach(activity);
      try {
        ActivitiesFragmentMarkerInterface mActivity = (ActivitiesFragmentMarkerInterface) activity;
      } catch (ClassCastException e) {
          throw new ClassCastException(activity.toString() + " must implement ActivitiesFragmentInterface");
      }
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.activity_addactivities_fragment_reminders, container, false);
      return rootView;
  }

}
