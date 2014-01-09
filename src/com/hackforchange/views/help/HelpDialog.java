package com.hackforchange.views.help;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.hackforchange.R;

public class HelpDialog extends DialogFragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_helpactivity, container, false);
    getDialog().setCanceledOnTouchOutside(true);
    
    Button closeButton = (Button) view.findViewById(R.id.closeButton); 
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
    
    WebView helpContent = (WebView) view.findViewById(R.id.helpContent);
    helpContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    helpContent.loadUrl("file:///android_asset/helpContent.html");
    
    return view;
  }

}
