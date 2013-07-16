package com.hackforchange.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.backend.reminders.RemindersDAO;
import com.hackforchange.models.activities.Participation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

// TODO: Make sure required text fields are not empty
public class RecordParticipationActivity extends Activity {
  private int participationid;
  private long dateTime;
  protected Button submitButton;
  protected EditText menNumText, womenNumText;
  protected CheckBox menCheckbox, womenCheckbox;
  protected Participation p;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.recordparticipationactivity);

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
    title.setText(new ActivitiesDAO(getApplicationContext()).getActivityWithId(new RemindersDAO(getApplicationContext()).getReminderWithId(p.getReminderid()).getActivityid()).getTitle());

    // display date and time for this reminder
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy, EEEE, hh:mm aaa"); // example: 07/04/2013, Thursday, 6:13 PM
    TextView datetime = (TextView) findViewById(R.id.datetime);
    datetime.setText(parser.format(c.getTime()));

    // TODO: make sure men and women counts are not empty if the corresponding checkboxes are checked
    menCheckbox = (CheckBox) findViewById(R.id.menCheckBox);
    womenCheckbox = (CheckBox) findViewById(R.id.womenCheckBox);
    menNumText = (EditText) findViewById(R.id.numMen);
    womenNumText = (EditText) findViewById(R.id.numWomen);

    submitButton = (Button) findViewById(R.id.submitbutton);
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // set men, women and serviced
        if(menCheckbox.isChecked()){
          if(menNumText.getText()!=null){
            p.setMen(Integer.parseInt(menNumText.getText().toString()));
          }
        }
        else{
          p.setMen(0);
        }

        if(womenCheckbox.isChecked()){
          if(womenNumText.getText()!=null){
            p.setWomen(Integer.parseInt(womenNumText.getText().toString()));
          }
        }
        else{
          p.setWomen(0);
        }

        // update the serviced flag for this Reminder in the Reminders table
        // so that the next time the NotificationReceiver checks, this participation
        // does not show up as unserviced
        p.setServiced(true);

        pDao.updateParticipation(p);

        finish();
      }
    });
  }
}