package com.hackforchange.views.welcome;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.hackforchange.R;

import java.util.List;

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
        HomeItemHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new HomeItemHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        } else
            holder = (HomeItemHolder) row.getTag();

        holder.txtTitle.setText(data.get(position));
        return row;
    }
}

class HomeItemHolder {
    TextView txtTitle;
}
