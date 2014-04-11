package com.hackforchange.views.participationsactive.signinsheet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.hackforchange.R;
import com.hackforchange.common.StyledButton;
import com.hackforchange.models.activities.Participant;

public class SignInSheetActivity extends SherlockFragmentActivity {
  private StyledButton submitButton, signButton;
  private Intent intent; 
  private EditText nameText, phoneText, villageText, ageText;
  private TextView signInMessage;
  private RadioButton maleRadioButton;
  private Bitmap signatureBitmap;
  private View spacer;
  SignatureDialog signatureDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signinsheet);
    intent = getIntent();
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    String signInMsg = intent.getExtras().getString("activitytitle");

    if(intent.hasExtra("participationdate")){ //we need to check because there's a chance this is not present (from RQPA if user does not enter date)
      signInMsg = signInMsg + " " + intent.getExtras().getString("participationdate");
    }

    signInMessage = (TextView) findViewById(R.id.pleasesignin);
    signInMessage.setText(signInMsg);

    nameText = (EditText) findViewById(R.id.nameEditText);
    phoneText = (EditText) findViewById(R.id.phoneEditText);
    villageText = (EditText) findViewById(R.id.villageEditText);
    ageText = (EditText) findViewById(R.id.ageEditText);
    submitButton = (StyledButton) findViewById(R.id.submitbutton);
    signButton = (StyledButton) findViewById(R.id.signbutton);
    maleRadioButton = (RadioButton) findViewById(R.id.maleRadioButton);
    spacer = findViewById(R.id.spacer);

    makeButtonsVisibleIfSignatureAvailable();

    signButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ageText.getText().length() == 0 || nameText.getText().length() == 0){
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage,
                  Toast.LENGTH_SHORT).show();
          return;
        }
        signatureDialog = new SignatureDialog();
        signatureDialog.setRetainInstance(true);
        signatureDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        signatureDialog.show(getSupportFragmentManager(), "signaturedialog");
      }
    });

    // go back to SignInSheetLandingActivity
    // send back the participant just entered
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Participant p = new Participant();
        p.setId(-1);  // the -1 indicates this is a participant NOT already in the database. Comes in handy if we're
        // editing an existing participation in RecordOrEditParticipationActivity
        // it plays no role in RecordQuickParticipationActivity because the only use case for
        // that activity is adding a new participation

        if (nameText.getText().length() == 0){
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage,
                  Toast.LENGTH_SHORT).show();
          return;
        }
        else
          p.setName(nameText.getText().toString());

        p.setPhoneNumber(phoneText.getText().toString());

        p.setVillage(villageText.getText().toString());

        if (ageText.getText().length() == 0){
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage,
                  Toast.LENGTH_SHORT).show();
          return;
        }
        else
          p.setAge(Integer.parseInt(ageText.getText().toString()));

        if(maleRadioButton.isChecked())
          p.setGender(Participant.MALE);
        else
          p.setGender(Participant.FEMALE);

        p.setSignatureBitmap(signatureBitmap);

        Bundle resultBundle = new Bundle();
        resultBundle.putParcelable("participant", p);
        intent.putExtras(resultBundle);
        setResult(RESULT_OK, intent);
        finish();
      }
    });
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // provide a back button on the actionbar
        finish();
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  /**
   * Callback for SignatureDialog
   * @param signatureBitmap
   */
  public void setScaledBitmap(Bitmap signatureBitmap) {
    this.signatureBitmap = signatureBitmap;
  }

  void makeButtonsVisibleIfSignatureAvailable() {
    if(signatureBitmap!=null){
      signButton.setText(getResources().getString(R.string.signagain));
      submitButton.setVisibility(View.VISIBLE);
      spacer.setVisibility(View.VISIBLE);
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle out) {
    super.onSaveInstanceState(out);
    if(signatureBitmap!=null)
      out.putParcelable("signatureBitmap", signatureBitmap);
  }

  @Override
  protected void onRestoreInstanceState(Bundle in) {
    super.onRestoreInstanceState(in);
    Bitmap savedSignatureBitmap = (Bitmap) in.getParcelable("signatureBitmap");
    if(savedSignatureBitmap!=null)
      setScaledBitmap(savedSignatureBitmap);
  }

}
