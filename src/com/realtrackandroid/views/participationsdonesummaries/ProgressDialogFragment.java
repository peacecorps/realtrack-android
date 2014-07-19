package com.realtrackandroid.views.participationsdonesummaries;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.realtrackandroid.R;

public class ProgressDialogFragment extends DialogFragment {
  private static ProgressDialogFragment mFragment;
  private static ProgressDialog progressDialog;
  
  public static ProgressDialogFragment newInstance() {
    if(mFragment==null) {
      mFragment = new ProgressDialogFragment();
    }
    return mFragment;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setMessage(getActivity().getString(R.string.generatingreports));
      progressDialog.setCancelable(false);
      return progressDialog;
  }
  
  @Override
  public void onDismiss(DialogInterface dialog){
    super.onDismiss(dialog);
    if(progressDialog.isShowing())
      progressDialog.dismiss();
  }
}