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
  private List<String> sectorList;
  private IndicatorsDAO iDao;
  private ArrayAdapter<String> sectorDataAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_collectpcvinfo);

    iDao = new IndicatorsDAO(this);

    final EditText nameEditText = (EditText) findViewById(R.id.nameEditText);

    final Spinner postSpinner = (Spinner) findViewById(R.id.postSpinner);
    List<String> postList = iDao.getAllPosts();
    ArrayAdapter<String> postDataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, postList);
    postDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    postSpinner.setAdapter(postDataAdapter);
    postSpinner.setOnItemSelectedListener(this);

    final Spinner sectorSpinner = (Spinner) findViewById(R.id.sectorSpinner);
    sectorList = iDao.getAllSectors();
    sectorDataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, sectorList);
    sectorDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    sectorSpinner.setAdapter(sectorDataAdapter);

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
        editor.putString("name", nameEditText.getText().toString());
        editor.putString("post", postSpinner.getSelectedItem().toString());
        editor.putString("sector", sectorSpinner.getSelectedItem().toString());
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
        List<String> newSectors = iDao.getAllSectorsForPost(selectedPost);
        sectorList.clear();
        sectorList.addAll(newSectors);
        sectorDataAdapter.notifyDataSetChanged();
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
