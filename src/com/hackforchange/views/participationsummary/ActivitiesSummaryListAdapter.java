package com.hackforchange.views.participationsummary;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.hackforchange.R;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.views.activities.AllParticipationsListAdapter;

import java.util.List;

public class ActivitiesSummaryListAdapter extends ArrayAdapter<ActivitiesHolder> {
  Context context;
  int layoutResourceId;
  List<ActivitiesHolder> data = null;
  View row;
  ListView participationSummaryListView;

  public ActivitiesSummaryListAdapter(Context context, int layoutResourceId, List<ActivitiesHolder> data) {
    super(context, layoutResourceId, data);
    this.layoutResourceId = layoutResourceId;
    this.context = context;
    this.data = data;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    row = convertView;
    ActivitiesViewHolder holder = null;

    if (row == null) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      row = inflater.inflate(layoutResourceId, parent, false);

      holder = new ActivitiesViewHolder();
      holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

      row.setTag(holder);
    } else
      holder = (ActivitiesViewHolder) row.getTag();

    ActivitiesHolder activities = data.get(position);
    holder.txtTitle.setText(activities.getTitle());
    List<Participation> participationList = activities.getParticipationList();

    participationSummaryListView = (ListView) row.findViewById(R.id.participationsummarylistview);
    participationSummaryListView.setAdapter(new AllParticipationsListAdapter(context, R.layout.row_allparticipation, participationList));

    return row;
  }

  class ActivitiesViewHolder {
    TextView txtTitle;
  }
}
