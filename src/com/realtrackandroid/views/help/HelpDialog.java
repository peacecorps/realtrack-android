package com.realtrackandroid.views.help;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.realtrackandroid.R;

public class HelpDialog extends DialogFragment {

  private String displayUrl = "file:///android_asset/helpContent.html";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_help, container, false);
    getDialog().setCanceledOnTouchOutside(true);

    // displayUrl = "file:///android_asset/helpContent.html";

    Button closeButton = (Button) view.findViewById(R.id.closeButton);
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    WebView helpContent = (WebView) view.findViewById(R.id.helpContent);
    helpContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    helpContent.loadUrl(displayUrl);

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    if (getDialog() == null)
      return;

    getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
  }

  public String getDisplayUrl() {
    return displayUrl;
  }

  public void setDisplayUrl(String displayUrl) {
    this.displayUrl = displayUrl;
  }

}
