package com.hackforchange.views.activities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.projects.ProjectDAO;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.models.projects.Project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// presents a simple overview of a participation event. Used for the 'Pending' button on the home
// screen. This is different from AllParticipationsListAdapter because AllParticipationsListAdapter
// shows the details of the participation e.g. the number of men, women taking part, the day etc
public class ParticipationListAdapter extends ArrayAdapter<Participation> {
    Context context;
    int layoutResourceId;
    List<Participation> data = null;
    View row;

    public ParticipationListAdapter(Context context, int layoutResourceId, List<Participation> data) {
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
            //holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.projectTitle = (TextView) row.findViewById(R.id.projectTitle);
            holder.activityTitle = (TextView) row.findViewById(R.id.activityTitle);
            holder.participationDate = (TextView) row.findViewById(R.id.participationDate);

            row.setTag(holder);
        } else
            holder = (ParticipationHolder) row.getTag();

        Participation participation = data.get(position);
        // Participation -> Activities
        ActivitiesDAO aDao = new ActivitiesDAO(getContext());
        Activities activity = aDao.getActivityWithId(participation.getActivityid());
        // Activities -> Project
        ProjectDAO pDao = new ProjectDAO(getContext());
        Project project = pDao.getProjectWithId(activity.getProjectid());

        holder.projectTitle.setText(project.getTitle());
        holder.activityTitle.setText(activity.getTitle());
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy, EEEE, hh:mm aaa"); // example: 07/04/2013, Thursday, 6:13 PM
        Date d = new Date(participation.getDate());
        holder.participationDate.setText(parser.format(d));

        return row;
    }
}

class ParticipationHolder {
    //ImageView imgIcon;
    TextView projectTitle;
    TextView activityTitle;
    TextView participationDate;
}
