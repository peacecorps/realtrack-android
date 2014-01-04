package com.hackforchange.views.dialogs;

/*
 * Ensures that any activity calling the date picker dialog provides a way for the dialog to
 * pass back the date selected by the user.
 */
public interface PickDateDialogListener {
  public void setDate(String date);
}
