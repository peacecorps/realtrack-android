package com.realtrackandroid.views.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GlossaryDialog extends HelpDialog {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    displayUrl = "file:///android_asset/glossary.html";
    return super.onCreateView(inflater, container, savedInstanceState);
  }

}
