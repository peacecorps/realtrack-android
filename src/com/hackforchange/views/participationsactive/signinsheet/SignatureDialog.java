package com.hackforchange.views.participationsactive.signinsheet;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.hackforchange.R;
import com.hackforchange.common.StyledButton;

public class SignatureDialog extends SherlockDialogFragment {
  private StyledButton saveButton, clearButton;
  private SignatureView signatureView;
  private SignInSheetActivity mSignInSheetActivity;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_signature, container, false);
    
    saveButton = (StyledButton) view.findViewById(R.id.submitbutton);
    clearButton = (StyledButton) view.findViewById(R.id.clearButton);
    signatureView = (SignatureView) view.findViewById(R.id.signatureview);

    saveButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Bitmap signatureBitmap = signatureView.getSignature();
        if(signatureBitmap==null){
          Toast.makeText(getSherlockActivity(), getResources().getString(R.string.pleasesignfirst), Toast.LENGTH_SHORT).show();
          return;
        }
        try {
          mSignInSheetActivity = (SignInSheetActivity) getActivity();
          mSignInSheetActivity.setScaledBitmap(signatureBitmap);
          getDialog().dismiss();
        } catch (ClassCastException e) {
          throw new ClassCastException("This class can only be called from SignInSheetActivity");
        }
      }
    });

    clearButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        signatureView.eraseSignature();
      }
    });

    return view;
  }
  
  @Override
  public void onStart() {
    super.onStart();

    if (getDialog() == null) {
      return;
    }
    
    getDialog().getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
  }
   

}
