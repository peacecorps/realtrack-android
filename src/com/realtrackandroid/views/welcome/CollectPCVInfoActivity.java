package com.realtrackandroid.views.welcome;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.indicators.IndicatorsDAO;
import com.realtrackandroid.common.StyledButton;

/**
 * Start-up screen that asks PCV for his/her name + Post + Sector of their project
 * @author Raj
 */
public class CollectPCVInfoActivity extends SherlockActivity implements OnItemSelectedListener {
  private List<String> projectList;
  private IndicatorsDAO iDao;
  private ArrayAdapter<String> projectDataAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_collectpcvinfo);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    iDao = new IndicatorsDAO(this);

    final EditText nameEditText = (EditText) findViewById(R.id.nameEditText);

    final Spinner postSpinner = (Spinner) findViewById(R.id.postSpinner);
    List<String> postList = iDao.getAllPosts();
    ArrayAdapter<String> postDataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, postList);
    postDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    postSpinner.setAdapter(postDataAdapter);
    postSpinner.setOnItemSelectedListener(this);

    final Spinner projectSpinner = (Spinner) findViewById(R.id.projectSpinner);
    projectList = iDao.getAllSectors();
    projectDataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, projectList);
    projectDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    projectSpinner.setAdapter(projectDataAdapter);

    StyledButton submitButton = (StyledButton) findViewById(R.id.submitbutton);
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(nameEditText.getText().length()==0){
          Toast.makeText(CollectPCVInfoActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
          return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CollectPCVInfoActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.name), nameEditText.getText().toString());
        editor.putString(getString(R.string.post), postSpinner.getSelectedItem().toString());
        editor.putString(getString(R.string.project), projectSpinner.getSelectedItem().toString());
        editor.commit();
        overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
        Intent i = new Intent(CollectPCVInfoActivity.this, WelcomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        CollectPCVInfoActivity.this.startActivity(i);
        CollectPCVInfoActivity.this.onBackPressed();
      }
    });
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    switch(parent.getId()){
      case R.id.postSpinner:
        // if the post changes, update the associated sectors
        String selectedPost = parent.getItemAtPosition(position).toString();
        List<String> newProjects = iDao.getAllProjectsForPost(selectedPost);
        projectList.clear();
        projectList.addAll(newProjects);
        projectDataAdapter.notifyDataSetChanged();
        break;
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }
}
