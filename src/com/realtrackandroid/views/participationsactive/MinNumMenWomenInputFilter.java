package com.realtrackandroid.views.participationsactive;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Once a certain number of participants have been entered via the sign-in sheet,
 * the PCV must not be able to manually make the number of participants in
 * RecordQuickParticipation or RecordParticipation less than that number.
 * 
 * @author Raj
 */
public class MinNumMenWomenInputFilter implements InputFilter {

  private int min;

  public MinNumMenWomenInputFilter(int min) {
    this.min = min;
  }
  public MinNumMenWomenInputFilter(String min) {
    this.min = Integer.parseInt(min);
  }

  @Override
  public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
    String currentVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
    currentVal = currentVal.substring(0, dstart) + source.toString() + currentVal.substring(dstart, currentVal.length());
    
    if(currentVal.length()==0)
      return dest.toString().substring(dstart,dend);
    
    int currentNumMenWomen = Integer.parseInt(currentVal);
    if(currentNumMenWomen>=min)
      return null; //accept the newly entered value
    else
      return dest.toString().substring(dstart,dend);
  }

}
