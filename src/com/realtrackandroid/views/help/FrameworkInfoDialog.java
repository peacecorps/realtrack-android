package com.realtrackandroid.views.help;

import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.realtrackandroid.R;
import com.realtrackandroid.backend.indicators.IndicatorsDAO;
import com.realtrackandroid.models.indicators.Indicators;

public class FrameworkInfoDialog extends DialogFragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_frameworkinfo, container, false);
    getDialog().setCanceledOnTouchOutside(true);

    IndicatorsDAO iDao = new IndicatorsDAO(getActivity());

    TextView frameworkContent = (TextView) view.findViewById(R.id.frameworkContent);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    String post = prefs.getString(getString(R.string.post), "");
    String project = prefs.getString(getString(R.string.project), "");

    List<Indicators> indicatorList = iDao.getAllIndicatorsForPostAndProject(post, project);

    StringBuilder sb = new StringBuilder();
    sb.append("Project Framework");
    addNewline(sb);
    sb.append(project + ", " + post);

    frameworkContent.setText(sb.toString());

    Button closeButton = (Button) view.findViewById(R.id.closeButton);
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    if (getDialog() == null)
      return;
    
    getDialog().getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
  }

  private void addNewline(StringBuilder sb) {
    sb.append("\n");
  }
}
