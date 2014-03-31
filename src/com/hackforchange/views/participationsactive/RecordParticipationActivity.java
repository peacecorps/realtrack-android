package com.hackforchange.views.participationsactive;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.actionbarsherlock.app.SherlockActivity;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.models.activities.Participation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RecordParticipationActivity extends SherlockActivity {
  private int participationid;
  private long dateTime;
  protected Button submitButton, dismissButton;
  protected EditText menNumText, men1524NumText, menOver24NumText, womenNumText, women1524NumText, womenOver24NumText, notesText;
  protected CheckBox menCheckbox, men1524Checkbox, menOver24Checkbox, womenCheckbox, women1524Checkbox, womenOver24Checkbox;
  protected Participation p;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recordparticipation);

    // read in the ID of the activities for which we're recording participation
    participationid = getIntent().getExtras().getInt("participationid");

    // also note the date and time
    dateTime = getIntent().getExtras().getLong("datetime");
  }

  @Override
  public void onResume() {
    super.onResume();
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(dateTime);

    final ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());
    p = pDao.getParticipationWithId(participationid);

    // display title for this activity
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(new ActivitiesDAO(getApplicationContext()).getActivityWithId(p.getActivityid()).getTitle());

    // display date and time for this reminder
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy, EEEE, hh:mm aaa"); // example: 07/04/2013, Thursday, 6:13 PM
    TextView datetime = (TextView) findViewById(R.id.datetime);
    datetime.setText(parser.format(c.getTime()));

    menCheckbox = (CheckBox) findViewById(R.id.menCheckBox);
    men1524Checkbox = (CheckBox) findViewById(R.id.men1524CheckBox);
    menOver24Checkbox = (CheckBox) findViewById(R.id.menOver24CheckBox);
    womenCheckbox = (CheckBox) findViewById(R.id.womenCheckBox);
    women1524Checkbox = (CheckBox) findViewById(R.id.women1524CheckBox);
    womenOver24Checkbox = (CheckBox) findViewById(R.id.womenOver24CheckBox);
    menNumText = (EditText) findViewById(R.id.numMen);
    men1524NumText = (EditText) findViewById(R.id.numMen1524);
    menOver24NumText = (EditText) findViewById(R.id.numMenOver24);
    womenNumText = (EditText) findViewById(R.id.numWomen);
    women1524NumText = (EditText) findViewById(R.id.numWomen1524);
    womenOver24NumText = (EditText) findViewById(R.id.numWomenOver24);
    notesText = (EditText) findViewById(R.id.notes);
    submitButton = (Button) findViewById(R.id.submitbutton);
    dismissButton = (Button) findViewById(R.id.dismissButton);

    menCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!menCheckbox.isChecked())
          menNumText.setText("");
      }
    });

    men1524Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!men1524Checkbox.isChecked())
          men1524NumText.setText("");
      }
    });

    menOver24Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!menOver24Checkbox.isChecked())
          menOver24NumText.setText("");
      }
    });

    womenCheckbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!womenCheckbox.isChecked())
          womenNumText.setText("");
      }
    });

    women1524Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!women1524Checkbox.isChecked())
          women1524NumText.setText("");
      }
    });

    womenOver24Checkbox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!womenOver24Checkbox.isChecked())
          womenOver24NumText.setText("");
      }
    });
    
    dismissButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        pDao.deleteParticipation(participationid);
        finish();
      }
    });

    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!menCheckbox.isChecked() && !men1524Checkbox.isChecked()
                && !menOver24Checkbox.isChecked() && !womenCheckbox.isChecked()
                && !women1524Checkbox.isChecked() && !womenOver24Checkbox.isChecked()) {
          Toast.makeText(getApplicationContext(), R.string.emptyfieldserrormessage,
                  Toast.LENGTH_SHORT).show();
          return;
        }

        // set men, women and serviced
        if (menCheckbox.isChecked()) {
          if (menNumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setMen(Integer.parseInt(menNumText.getText().toString()));
        }
        else {
          p.setMen(0);
        }

        if (men1524Checkbox.isChecked()) {
          if (men1524NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setMen1524(Integer.parseInt(men1524NumText.getText().toString()));
        }
        else {
          p.setMen1524(0);
        }

        if (menOver24Checkbox.isChecked()) {
          if (menOver24NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setMenOver24(Integer.parseInt(menOver24NumText.getText().toString()));
        }
        else {
          p.setMenOver24(0);
        }

        if (womenCheckbox.isChecked()) {
          if (womenNumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setWomen(Integer.parseInt(womenNumText.getText().toString()));
        }
        else {
          p.setWomen(0);
        }

        if (women1524Checkbox.isChecked()) {
          if (women1524NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setWomen1524(Integer.parseInt(women1524NumText.getText().toString()));
        }
        else {
          p.setWomen1524(0);
        }

        if (womenOver24Checkbox.isChecked()) {
          if (womenOver24NumText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), R.string.emptyparticipationmessage,
                    Toast.LENGTH_SHORT).show();
            return;
          }
          else
            p.setWomenOver24(Integer.parseInt(womenOver24NumText.getText().toString()));
        }
        else {
          p.setWomenOver24(0);
        }

        p.setNotes(notesText.getText().toString());

        // update the serviced flag for this Reminder in the Reminders table
        // so that the next time the NotificationReceiver checks, this participation
        // does not show up as unserviced
        p.setServiced(true);

        pDao.updateParticipation(p);

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
}