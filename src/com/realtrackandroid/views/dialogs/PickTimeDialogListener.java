package com.realtrackandroid.views.dialogs;

/*
 * Ensures that any activity calling the time picker dialog provides a way for the dialog to
 * pass back the time selected by the user.
 */
public interface PickTimeDialogListener {
  public void setTime(String time);
}