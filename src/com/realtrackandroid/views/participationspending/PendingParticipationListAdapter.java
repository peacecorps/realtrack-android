package com.realtrackandroid.views.participationspending;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ActivitiesDAO;
import com.realtrackandroid.backend.projects.ProjectDAO;
import com.realtrackandroid.common.StyledButton;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.activities.Participation;
import com.realtrackandroid.models.projects.Project;
import com.realtrackandroid.views.participationsactive.RecordOrEditParticipationActivity;

// presents a simple overview of a participation event. Used for the 'Pending' button on the home
// screen. This is different from AllParticipationsListAdapter because AllParticipationsListAdapter
// shows the details of the participation e.g. the number of men, women taking part, the day etc
public class PendingParticipationListAdapter extends ArrayAdapter<Participation> {
  Context context;
  int layoutResourceId;
  List<Participation> data = null;
  View row;

  public PendingParticipationListAdapter(Context context, int layoutResourceId, List<Participation> data) {
    super(context, layoutResourceId, data);
    this.layoutResourceId = layoutResourceId;
    this.context = context;
    this.data = data;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    row = convertView;
    ParticipationHolder holder = null;

    if (row == null) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      row = inflater.inflate(layoutResourceId, parent, false);

      holder = new ParticipationHolder();
      holder.projectTitle = (TextView) row.findViewById(R.id.projectTitle);
      holder.activityTitle = (TextView) row.findViewById(R.id.activityTitle);
      holder.participationDate = (TextView) row.findViewById(R.id.participationDate);
      holder.participationTime = (TextView) row.findViewById(R.id.participationTime);
      holder.addDetailsButton = (StyledButton) row.findViewById(R.id.adddetailsbutton);

      row.setTag(holder);
    } else
      holder = (ParticipationHolder) row.getTag();
    
    final int pos = position;
    
    holder.addDetailsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Participation p = data.get(pos);
        // if the user is already on the pending participations screen when a notification pops up or comes to it
        // from the home screen's "Pending" button (and not by clicking the notifification), clear the corresponding
        // notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
        notificationManager.cancel(p.getId());
        // clicking on the item must take the user to the record participation activity
        Intent newActivity = new Intent(context, RecordOrEditParticipationActivity.class);
        newActivity.putExtra("participationid", p.getId());
        newActivity.putExtra("datetime", p.getDate());
        context.startActivity(newActivity);
        ((PendingParticipationActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
      }
    });

    Participation participation = data.get(position);
    // Participation -> Activities
    ActivitiesDAO aDao = new ActivitiesDAO(getContext());
    Activities activity = aDao.getActivityWithId(participation.getActivityid());
    // Activities -> Project
    ProjectDAO pDao = new ProjectDAO(getContext());
    Project project = pDao.getProjectWithId(activity.getProjectid());

    holder.projectTitle.setText(project.getTitle());
    holder.activityTitle.setText(activity.getTitle());
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy (EEEE)");
    Date d = new Date(participation.getDate());
    holder.participationDate.setText(parser.format(d));

    parser = new SimpleDateFormat("hh:mm aaa");
    holder.participationTime.setText(parser.format(d));
    return row;
  }

  private class ParticipationHolder {
    TextView projectTitle;
    TextView activityTitle;
    TextView participationDate;
    TextView participationTime;
    StyledButton addDetailsButton;
  }
}

