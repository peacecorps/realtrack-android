package com.hackforchange.views.participationsactive.signinsheet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.hackforchange.R;
import com.hackforchange.R.id;
import com.hackforchange.R.layout;

/*
 * This class provides a landing page for the sign in sheets
 */
public class SignInSheetLandingActivity extends SherlockFragmentActivity {
  private Button okButton;
  private Button doneButton;
  
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signinsheetlandingactivity);
  }

  @Override
  public void onResume() {
    super.onResume();
    
    okButton = (Button) findViewById(R.id.okbutton);
    doneButton = (Button) findViewById(R.id.donebutton);
    
    okButton.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View arg0) {
        // TODO Auto-generated method stub
        
      }
    });
    
    doneButton.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View arg0) {
        // TODO Auto-generated method stub
        
      }
    });
    
  }

}
