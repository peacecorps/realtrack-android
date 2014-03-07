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
import com.hackforchange.backend.projects.ProjectDAO;
import com.hackforchange.models.projects.Project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Presents an activity that displays details of an existing project
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