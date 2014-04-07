package com.hackforchange.views.participationsactive.signinsheet;

import com.hackforchange.R;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SignatureDialog extends DialogFragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_signature, container, false);
    getDialog().setCanceledOnTouchOutside(false);
    
    /*Button closeButton = (Button) view.findViewById(R.id.closeButton); 
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });*/
    
    return view;
  }

}
