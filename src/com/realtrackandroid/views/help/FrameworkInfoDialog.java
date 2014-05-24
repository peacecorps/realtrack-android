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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.indicators.IndicatorsDAO;
import com.realtrackandroid.models.indicators.Indicators;

public class FrameworkInfoDialog extends DialogFragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_help, container, false);
    getDialog().setCanceledOnTouchOutside(true);

    IndicatorsDAO iDao = new IndicatorsDAO(getActivity());

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    String post = prefs.getString(getString(R.string.post), "");
    String project = prefs.getString(getString(R.string.project), "");

    List<Indicators> indicatorList = iDao.getAllIndicatorsForPostAndProject(post, project);
    
    WebView helpContent = (WebView) view.findViewById(R.id.helpContent);
    helpContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    helpContent.loadData(createFrameworkContent(post, project, indicatorList), "text/html", "UTF-8");

    Button closeButton = (Button) view.findViewById(R.id.closeButton);
    closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    return view;
  }

  private String createFrameworkContent(String post, String project, List<Indicators> indicatorList) {
    StringBuilder sb = new StringBuilder();
    sb.append("<!DOCTYPE html>" +
    		"<html>" +
    		"<head>" +
    		"<style>" +
    		"div.header{" +
    		"text-align:center;" +
    		"background-color:#0099CC;" +
    		"color:white;" +
    		"padding:2px;" +
    		"}" +
    		"</style>" +
    		"</head>" +
    		"<body>" +
    		"<div class='header'>");
    sb.append("<strong>Project Framework</strong>");
    addNewline(sb);
    sb.append("<br>"+project + ", " + post);
    sb.append("</div><br>");
    String _goal = "";
    String _objective = "";
    String _indicator = "";
    
    //the list is already sorted on goals
    for(Indicators i: indicatorList){
      String goal = i.getGoal();
      String objective = i.getObjective();
      String indicator = i.getIndicator();
      if(!goal.equals(_goal)){
        sb.append("</ul>"); //closes the last OBJECTIVE list
        sb.append("<strong>"+goal+"</strong>");
        sb.append("<ul>"); //opens the next OBJECTIVE list
        addNewline(sb);
        _goal = goal;
      }
      if(!objective.equals(_objective)){
        sb.append("</ul>"); //closes the last INDICATORS list
        sb.append("<li><strong>"+objective+"</strong></li>");
        sb.append("<br>Example Indicators:");
        addNewline(sb);
        sb.append("<ul>"); //opens the next INDICATORS list
        addNewline(sb);
        _objective = objective;
      }
      if(!indicator.equals(_indicator)){
        sb.append("<li>"+indicator);
        addNewline(sb);
        _objective = objective;
      }
    }
    
    sb.append("</body>" +
    		"</html>");
    return sb.toString();
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
