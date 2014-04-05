package com.hackforchange.views.welcome;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hackforchange.R;
import com.hackforchange.common.StyledButton;
import com.hackforchange.views.participationsdonesummaries.ParticipationSummaryActivity;
import com.hackforchange.views.participationspending.PendingParticipationActivity;
import com.hackforchange.views.projectsactivities.AllProjectsActivitiesActivity;

public class HomeItemListAdapter extends ArrayAdapter<String> {
    Context context;
    int layoutResourceId;
    List<String> data = null;
    View row;

    public HomeItemListAdapter(Context context, int layoutResourceId, List<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        row = convertView;
        final int pos = position;
        HomeItemHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new HomeItemHolder();
            holder.txtTitle = (StyledButton) row.findViewById(R.id.txtTitle);
            holder.txtTitle.setFocusable(false);
            holder.txtTitle.setFocusableInTouchMode(false);

            row.setTag(holder);
        } else
            holder = (HomeItemHolder) row.getTag();

        holder.txtTitle.setText(data.get(position));
        
        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            switch (pos) {
              case 0: // MY PROJECTS
                Intent newActivity = new Intent(context, AllProjectsActivitiesActivity.class);
                context.startActivity(newActivity);
                ((WelcomeActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
                break;
              case 1: // MY DATA
                newActivity = new Intent(context, ParticipationSummaryActivity.class);
                context.startActivity(newActivity);
                ((WelcomeActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
                break;
              case 2: // PENDING
                newActivity = new Intent(context, PendingParticipationActivity.class);
                context.startActivity(newActivity);
                ((WelcomeActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
                break;
            }
          }
        });
        
        return row;
    }
}

class HomeItemHolder {
    TextView txtTitle;
}
