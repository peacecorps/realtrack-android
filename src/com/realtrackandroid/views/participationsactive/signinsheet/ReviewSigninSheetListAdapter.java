package com.realtrackandroid.views.participationsactive.signinsheet;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.realtrackandroid.R;
import com.realtrackandroid.models.activities.Participant;

public class ReviewSigninSheetListAdapter extends ArrayAdapter<Participant> {
  Context context;

  int layoutResourceId;

  List<Participant> data = null;

  View row;

  public ReviewSigninSheetListAdapter(Context context, int layoutResourceId, List<Participant> data) {
    super(context, layoutResourceId, data);
    this.layoutResourceId = layoutResourceId;
    this.context = context;
    this.data = data;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    row = convertView;
    ParticipantHolder holder = null;

    if (row == null) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      row = inflater.inflate(layoutResourceId, parent, false);

      holder = new ParticipantHolder();
      holder.name = (TextView) row.findViewById(R.id.name);
      holder.village = (TextView) row.findViewById(R.id.village);

      row.setTag(holder);
    }
    else
      holder = (ParticipantHolder) row.getTag();

    Participant participant = data.get(position);
    holder.name.setText(participant.getName());
    holder.village.setText(participant.getVillage());

    return row;
  }
}

class ParticipantHolder {
  TextView name;

  TextView village;
}
