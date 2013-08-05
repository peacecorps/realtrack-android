package com.hackforchange.views.activities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.hackforchange.R;
import com.hackforchange.models.activities.Participation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// Shows the details of the participation e.g. the number of men, women taking part, the day etc.
// This is different from ParticipationListAdapter because ParticipationListAdapter presents a
// simple overview of a participation event (it is used for the 'Pending' button on the home screen).
public class AllParticipationsListAdapter extends ArrayAdapter<Participation> {
    Context context;
    int layoutResourceId;
    List<Participation> data = null;
    View row;

    public AllParticipationsListAdapter(Context context, int layoutResourceId, List<Participation> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        row = convertView;
        AllParticipationHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AllParticipationHolder();
            //holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.date = (TextView) row.findViewById(R.id.date);
            holder.time = (TextView) row.findViewById(R.id.time);
            holder.men = (TextView) row.findViewById(R.id.men);
            holder.women = (TextView) row.findViewById(R.id.women);

            row.setTag(holder);
        } else
            holder = (AllParticipationHolder) row.getTag();

        Participation participation = data.get(position);
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy"); // example: 07/04/2013
        Date d = new Date(participation.getDate());
        holder.date.setText(parser.format(d));
        parser = new SimpleDateFormat("hh:mm aaa"); // example: 6:13 PM
        holder.time.setText(parser.format(d));
        holder.men.setText(participation.getMen() + ""); // setText must always be passed a string or you'll have weird resourcenotfoundexceptions!!
        holder.women.setText(participation.getWomen() + ""); // setText must always be passed a string or you'll have weird resourcenotfoundexceptions!!

        return row;
    }
}

class AllParticipationHolder {
    //ImageView imgIcon;
    TextView date;
    TextView time;
    TextView men;
    TextView women;
}
