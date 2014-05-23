package com.realtrackandroid.views.welcome;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockActivity;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.indicators.IndicatorsDAO;

public class CollectPCVInfoActivity extends SherlockActivity implements OnItemSelectedListener {
  private List<String> sectorList;
  private IndicatorsDAO iDao;
  private ArrayAdapter<String> sectorDataAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_collectpcvinfo);
    
    iDao = new IndicatorsDAO(this);

    Spinner postSpinner = (Spinner) findViewById(R.id.postSpinner);
    List<String> postList = iDao.getAllPosts();
    ArrayAdapter<String> postDataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, postList);
    postDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    postSpinner.setAdapter(postDataAdapter);
    postSpinner.setOnItemSelectedListener(this);

    Spinner sectorSpinner = (Spinner) findViewById(R.id.sectorSpinner);
    sectorList = iDao.getAllSectors();
    sectorDataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, sectorList);
    sectorDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    sectorSpinner.setAdapter(sectorDataAdapter);
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
}
