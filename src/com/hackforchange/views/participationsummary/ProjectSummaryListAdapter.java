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

import java.util.List;

public class ProjectSummaryListAdapter extends ArrayAdapter<ProjectHolder> {
  Context context;
  int layoutResourceId;
  List<ProjectHolder> data = null;
  View row;
  ListView activitiesSummaryListView;

  public ProjectSummaryListAdapter(Context context, int layoutResourceId, List<ProjectHolder> data) {
    super(context, layoutResourceId, data);
    this.layoutResourceId = layoutResourceId;
    this.context = context;
    this.data = data;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    row = convertView;
    ProjectViewHolder holder = null;

    if (row == null) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      row = inflater.inflate(layoutResourceId, parent, false);

      holder = new ProjectViewHolder();
      holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

      row.setTag(holder);
    } else
      holder = (ProjectViewHolder) row.getTag();

    ProjectHolder project = data.get(position);
    holder.txtTitle.setText(project.getTitle());
    List<ActivitiesHolder> activitiesHolderList = project.getActivitiesHolderList();

    activitiesSummaryListView = (ListView) row.findViewById(R.id.activitiessummarylistview);
    activitiesSummaryListView.setAdapter(new ActivitiesSummaryListAdapter(context, R.layout.row_activitiessummary, activitiesHolderList));

    return row;

  }

  class ProjectViewHolder {
    TextView txtTitle;
  }
}