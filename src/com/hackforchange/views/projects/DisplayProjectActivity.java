package com.hackforchange.views.projects;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.projects.ProjectDAO;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.projects.Project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * Presents an activity that displays details of an existing activity
 * Also lets you edit the project (EditProjectActivity) or delete the project (right from this java file)
 * by choosing buttons in the ActionBar
 * Pressing the back key will exit the activity
 */
public class DisplayProjectActivity extends SherlockActivity {
    private int id;
    private Project p;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayproject);

        // read in the ID of the project that this activity must display details of
        id = getIntent().getExtras().getInt("projectid");
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ProjectDAO pDao = new ProjectDAO(getApplicationContext());
        p = pDao.getProjectWithId(id);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(p.getTitle());
        DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        Date d = new Date(p.getStartDate());
        TextView startDate = (TextView) findViewById(R.id.startDate);
        startDate.setText(parser.format(d));
        d = new Date(p.getEndDate());
        TextView endDate = (TextView) findViewById(R.id.endDate);
        endDate.setText(parser.format(d));
        TextView notes = (TextView) findViewById(R.id.notes);
        notes.setText(p.getNotes());

        ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());
        ArrayList<Activities> aList = aDao.getAllActivitiesForProjectId(id);
        /*Button showActivities = (Button) findViewById(R.id.showActivities);

        // if there are no activities associated as yet with this project, hide the "Show Activities" button
        // actually, we hide the linearlayout that holds it so that it takes up no space in the layout
        if (aList.size() == 0) {
            showActivities.setVisibility(View.GONE);
        } else {
            ((Button) findViewById(R.id.showActivities)).setVisibility(View.VISIBLE);
            // transition to new activity that shows all the activites associated with this project
            showActivities.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DisplayProjectActivity.this, AllActivitiesActivity.class);
                    i.putExtra("projectid", id);
                    startActivity(i);
                }
            });
        }*/

        /*TextView addActivities = (TextView) findViewById(R.id.addActivities);
        addActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayProjectActivity.this, AddActivitiesActivity.class);
                i.putExtra("projectid", id);
                startActivity(i);
            }
        });*/
    }

    // create actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.displayprojectmenu, menu);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        return true;
    }

    /**
     * ******************************************************************************************************************
     * transition to view for adding new project when the add icon in the action bar is clicked
     * ******************************************************************************************************************
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // provide a back button on the actionbar
                finish();
                break;
            case R.id.action_deleteproject:
                // warn the user first!
                new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to delete this project? This CANNOT be undone.")
                    .setCancelable(false)
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ProjectDAO pDao = new ProjectDAO(getApplicationContext());
                            pDao.deleteProject(DisplayProjectActivity.this.id);
                            finish();
                        }
                    })
                    .show();
                break;
            case R.id.action_editproject:
                Intent i = new Intent(DisplayProjectActivity.this, EditProjectActivity.class);
                i.putExtra("projectid", id);
                startActivity(i);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}