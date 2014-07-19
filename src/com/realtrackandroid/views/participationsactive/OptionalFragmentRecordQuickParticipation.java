package com.realtrackandroid.views.participationsactive;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.realtrackandroid.R;
import com.realtrackandroid.models.activities.Participation;

public class OptionalFragmentRecordQuickParticipation extends SherlockFragment {

  private EditText spmen09NumText, spmen1017NumText, spmen1824NumText, spmenOver25NumText,
          spwomen09NumText, spwomen1017NumText, spwomen1824NumText, spwomenOver25NumText,
          notesText;

  public static final OptionalFragmentRecordQuickParticipation newInstance(String title) {
    OptionalFragmentRecordQuickParticipation f = new OptionalFragmentRecordQuickParticipation();
    return f;
  }

  private View v;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      @SuppressWarnings("unused")
      RecordQuickParticipationFragmentInterface mActivity = (RecordQuickParticipationFragmentInterface) activity;
    }
    catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
              + " must implement RecordQuickParticipationFragmentInterface");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    v = inflater.inflate(R.layout.activity_recordquickparticipation_fragment_optional, container,
            false);
    return v;
  }

  @Override
  public void onResume() {
    super.onResume();

    spmen09NumText = (EditText) v.findViewById(R.id.numSpMen09);
    spmen1017NumText = (EditText) v.findViewById(R.id.numSpMen1017);
    spmen1824NumText = (EditText) v.findViewById(R.id.numSpMen1824);
    spmenOver25NumText = (EditText) v.findViewById(R.id.numSpMenOver25);
    spwomen09NumText = (EditText) v.findViewById(R.id.numSpWomen09);
    spwomen1017NumText = (EditText) v.findViewById(R.id.numSpWomen1017);
    spwomen1824NumText = (EditText) v.findViewById(R.id.numSpWomen1824);
    spwomenOver25NumText = (EditText) v.findViewById(R.id.numSpWomenOver25);

    notesText = (EditText) v.findViewById(R.id.notes);

  }

  public void setFields(Participation p) {
    if (v == null)
      return;

    if (spmen09NumText.getText().length() != 0)
      p.setSpMen09(Integer.parseInt(spmen09NumText.getText().toString()));
    else
      p.setSpMen09(0);

    if (spmen1017NumText.getText().length() != 0)
      p.setSpMen1017(Integer.parseInt(spmen1017NumText.getText().toString()));
    else
      p.setSpMen1017(0);

    if (spmen1824NumText.getText().length() != 0)
      p.setSpMen1824(Integer.parseInt(spmen1824NumText.getText().toString()));
    else
      p.setSpMen1824(0);

    if (spmenOver25NumText.getText().length() != 0)
      p.setSpMenOver25(Integer.parseInt(spmenOver25NumText.getText().toString()));
    else
      p.setSpMenOver25(0);

    if (spwomen09NumText.getText().length() != 0)
      p.setSpWomen09(Integer.parseInt(spwomen09NumText.getText().toString()));
    else
      p.setSpWomen09(0);

    if (spwomen1017NumText.getText().length() != 0)
      p.setSpWomen1017(Integer.parseInt(spwomen1017NumText.getText().toString()));
    else
      p.setSpWomen1017(0);

    if (spwomen1824NumText.getText().length() != 0)
      p.setSpWomen1824(Integer.parseInt(spwomen1824NumText.getText().toString()));
    else
      p.setSpWomen1824(0);

    if (spwomenOver25NumText.getText().length() != 0)
      p.setSpWomenOver25(Integer.parseInt(spwomenOver25NumText.getText().toString()));
    else
      p.setSpWomenOver25(0);

    p.setNotes(notesText.getText().toString());

  }
}
