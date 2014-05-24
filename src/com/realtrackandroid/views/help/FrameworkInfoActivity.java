package com.realtrackandroid.views.help;

import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.indicators.IndicatorsDAO;
import com.realtrackandroid.models.indicators.Indicators;

public class FrameworkInfoActivity extends SherlockActivity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_frameworkinfo);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    IndicatorsDAO iDao = new IndicatorsDAO(this);
    
    TextView frameworkContent = (TextView) findViewById(R.id.frameworkContent);
    
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String post = prefs.getString(getString(R.string.post), "");
    String project = prefs.getString(getString(R.string.project), "");
    
    List<Indicators> indicatorList = iDao.getAllIndicatorsForPostAndProject(post, project);
    
    StringBuilder sb = new StringBuilder();
    sb.append("Project Framework");
    addNewline(sb);
    sb.append(project+", "+post);
    
    frameworkContent.setText(sb.toString());
  }

  private void addNewline(StringBuilder sb) {
    sb.append("\n");
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }
}
