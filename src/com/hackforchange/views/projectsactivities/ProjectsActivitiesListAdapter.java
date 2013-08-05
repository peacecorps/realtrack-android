package com.hackforchange.views.projectsactivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.projects.ProjectDAO;
import com.hackforchange.backend.reminders.RemindersDAO;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.projects.Project;
import com.hackforchange.models.reminders.Reminders;
import com.hackforchange.views.activities.AddActivitiesActivity;
import com.hackforchange.views.activities.DisplayActivitiesActivity;
import com.hackforchange.views.activities.EditActivitiesActivity;
import com.hackforchange.views.projects.DisplayProjectActivity;
import com.hackforchange.views.projects.EditProjectActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectsActivitiesListAdapter extends BaseExpandableListAdapter {
  Context context;
  int groupLayoutResourceId;
  int childLayoutResourceId;
  List<ProjectsActivitiesHolder> projectsActivitiesData = null;
  View row;
  LayoutInflater inflater;

  public ProjectsActivitiesListAdapter(Context context, int groupLayoutResourceId, int childLayoutResourceId, List<ProjectsActivitiesHolder> projectsActivitiesData) {
    super();
    this.groupLayoutResourceId = groupLayoutResourceId;
    this.childLayoutResourceId = childLayoutResourceId;
    this.context = context;
    this.projectsActivitiesData = projectsActivitiesData;

  }

  public void setInflater(LayoutInflater inflater) {
    this.inflater = inflater;
  }

  @Override
  public int getGroupCount() {
    return projectsActivitiesData.size();
  }

  @Override
  public int getChildrenCount(int groupPosition) {
    return projectsActivitiesData.get(groupPosition).getActivitiesList().size();
  }

  @Override
  public Object getGroup(int groupPosition) {
    return projectsActivitiesData.get(groupPosition);
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    return projectsActivitiesData.get(groupPosition).getActivitiesList().get(childPosition);
  }

  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  @Override
  public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    final int groupPos = groupPosition;
    row = convertView;
    Holder holder = null;

    if (row == null) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      row = inflater.inflate(groupLayoutResourceId, parent, false);

      holder = new Holder();
      holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
      holder.startDate = (TextView) row.findViewById(R.id.startDate);

      row.setTag(holder);
    } else
      holder = (Holder) row.getTag();

    final Project project = projectsActivitiesData.get(groupPosition).getProject();
    holder.txtTitle.setText(project.getTitle());
    DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
    Date startDate = new Date(project.getStartDate());
    Date endDate = new Date(project.getEndDate());
    holder.startDate.setText(parser.format(startDate) + " to " + parser.format(endDate));

    // handle click on the title and date text views and show project details
    holder.txtTitle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(context, DisplayProjectActivity.class);
        i.putExtra("projectid", project.getId());
        context.startActivity(i);
      }
    });
    holder.startDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(context, DisplayProjectActivity.class);
        i.putExtra("projectid", project.getId());
        context.startActivity(i);
      }
    });

    // handle click on the edit project icon
    ImageView editActivityBtn = (ImageView) row.findViewById(R.id.editProjectBtn);
    editActivityBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(context, EditProjectActivity.class);
        i.putExtra("projectid", project.getId());
        context.startActivity(i);
      }
    });

    // handle click on the delete project icon
    ImageView deleteActivityBtn = (ImageView) row.findViewById(R.id.deleteProjectBtn);
    deleteActivityBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // warn the user first!
        new AlertDialog.Builder(context)
          .setMessage("Are you sure you want to delete this project? This CANNOT be undone.")
          .setCancelable(false)
          .setNegativeButton("No", null)
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              ProjectDAO pDao = new ProjectDAO(context);
              pDao.deleteProject(project.getId());

              // make sure the list gets updated in the display
              projectsActivitiesData.remove(groupPos);
              notifyDataSetChanged();
            }
          })
          .show();
      }
    });

    return row;
  }

  @Override
  public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    final int groupPos = groupPosition;
    final int childPos = childPosition;
    row = convertView;
    Holder holder = null;

    if (row == null) {
      row = inflater.inflate(childLayoutResourceId, parent, false);

      holder = new Holder();
      holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
      holder.startDate = (TextView) row.findViewById(R.id.startDate);

      row.setTag(holder);
    } else
      holder = (Holder) row.getTag();

    final Activities activities = projectsActivitiesData.get(groupPosition).getActivitiesList().get(childPosition);
    holder.txtTitle.setText(activities.getTitle());

    if (activities.getId() == -1) { //"add new activity..." item. hide the date field, the edit activity icon and the delete activity icon
      holder.startDate.setVisibility(View.GONE);
      row.findViewById(R.id.editActivityBtn).setVisibility(View.GONE);
      row.findViewById(R.id.deleteActivityBtn).setVisibility(View.GONE);

      // handle click on the title text view and show activity details
      holder.txtTitle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, AddActivitiesActivity.class);
          i.putExtra("projectid", activities.getProjectid());
          context.startActivity(i);
        }
      });
    }
    else { // normal activity item
      DateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
      Date startDate = new Date(activities.getStartDate());
      Date endDate = new Date(activities.getEndDate());
      holder.startDate.setText(parser.format(startDate) + " to " + parser.format(endDate));

      // handle click on the title and date text views and show activity details
      holder.txtTitle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, DisplayActivitiesActivity.class);
          i.putExtra("activitiesid", activities.getId());
          context.startActivity(i);
        }
      });
      holder.startDate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, DisplayActivitiesActivity.class);
          i.putExtra("activitiesid", activities.getId());
          context.startActivity(i);
        }
      });

      // handle click on the edit activity icon
      ImageView editActivityBtn = (ImageView) row.findViewById(R.id.editActivityBtn);
      editActivityBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, EditActivitiesActivity.class);
          i.putExtra("activitiesid", activities.getId());
          context.startActivity(i);
        }
      });

      // handle click on the delete activity icon
      ImageView deleteActivityBtn = (ImageView) row.findViewById(R.id.deleteActivityBtn);
      deleteActivityBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          // warn the user first!
          new AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete this activity? This CANNOT be undone.")
            .setCancelable(false)
            .setNegativeButton("No", null)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                ActivitiesDAO aDao = new ActivitiesDAO(context);
                int activityId = activities.getId();
                aDao.deleteActivities(activityId);
                // cancel all alarms for participation events of the reminders of this activity
                RemindersDAO rDao = new RemindersDAO(context);
                ArrayList<Reminders> reminders_data;
                reminders_data = rDao.getAllRemindersForActivityId(activityId);
                for (Reminders r : reminders_data) {
                  EditActivitiesActivity.deleteAlarmsForReminder(context, r.getId());
                }

                // make sure the list gets updated in the display
                projectsActivitiesData.get(groupPos).getActivitiesList().remove(childPos);
                notifyDataSetChanged();
              }
            })
            .show();
        }
      });
    }


    return row;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return false; //must be true if you want child to be clickable!
  }
}

class ProjectsActivitiesHolder {
  Project project;
  List<Activities> activitiesList;

  Project getProject() {
    return project;
  }

  void setProject(Project project) {
    this.project = project;
  }

  List<Activities> getActivitiesList() {
    return activitiesList;
  }

  void setActivitiesList(List<Activities> activitiesList) {
    this.activitiesList = activitiesList;
  }
}

class Holder {
  TextView txtTitle;
  TextView startDate;
  //TextView endDate;
}