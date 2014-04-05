package com.hackforchange.views.projectsactivities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.backend.projects.ProjectDAO;
import com.hackforchange.common.StyledButton;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.projects.Project;
import com.hackforchange.views.activities.AddActivitiesActivity;
import com.hackforchange.views.activities.DisplayActivitiesActivity;
import com.hackforchange.views.activities.EditActivitiesActivity;
import com.hackforchange.views.participationsactive.RecordQuickParticipationActivity;
import com.hackforchange.views.projects.AddProjectActivity;
import com.hackforchange.views.projects.DisplayProjectActivity;
import com.hackforchange.views.projects.EditProjectActivity;

public class ProjectsActivitiesListAdapter extends BaseExpandableListAdapter {
  Context context;
  int groupLayoutResourceId;
  int childLayoutResourceId;
  List<ProjectsActivitiesHolder> projectsActivitiesData = null;
  View row;
  LayoutInflater inflater;
  ExpandableListView listView;

  public ProjectsActivitiesListAdapter(Context context, int groupLayoutResourceId, int childLayoutResourceId, ExpandableListView expandableListView, List<ProjectsActivitiesHolder> projectsActivitiesData) {
    super();
    this.groupLayoutResourceId = groupLayoutResourceId;
    this.childLayoutResourceId = childLayoutResourceId;
    this.context = context;
    listView = expandableListView;
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
    final boolean isExp = isExpanded;
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

    if(project.getId() == -1){ //"add new project..." item. hide the date field, the edit activity icon and the delete activity icon
      row.findViewById(R.id.editProjectBtn).setVisibility(View.INVISIBLE);
      row.findViewById(R.id.deleteProjectBtn).setVisibility(View.INVISIBLE);
      row.findViewById(R.id.startDate).setVisibility(View.GONE);
      
      ((StyledButton)row.findViewById(R.id.expandCollapseProjectBtn)).setText(context.getResources().getString(R.string.fa_rightchevron));

      // handle click on the title text view and show activity details
      holder.txtTitle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, AddProjectActivity.class);
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });
    }
    else {
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
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      holder.startDate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, DisplayProjectActivity.class);
          i.putExtra("projectid", project.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });
      
      // expand and collapse groups
      final StyledButton expandCollapseProjectBtn = (StyledButton) row.findViewById(R.id.expandCollapseProjectBtn);
      expandCollapseProjectBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(isExp){
            listView.collapseGroup(groupPos);
            expandCollapseProjectBtn.setText(context.getResources().getString(R.string.fa_rightchevron));
          }
          else{
            listView.expandGroup(groupPos);
            expandCollapseProjectBtn.setText(context.getResources().getString(R.string.fa_downchevron));
          }
        }
      });

      // handle click on the edit project icon
      StyledButton editProjectBtn = (StyledButton) row.findViewById(R.id.editProjectBtn);
      editProjectBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, EditProjectActivity.class);
          i.putExtra("projectid", project.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      // handle click on the delete project icon
      StyledButton deleteProjectBtn = (StyledButton) row.findViewById(R.id.deleteProjectBtn);
      deleteProjectBtn.setOnClickListener(new View.OnClickListener() {
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
      
    }
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
      row.findViewById(R.id.quickParticipationBtn).setVisibility(View.GONE);

      // handle click on the title text view and show activity details
      holder.txtTitle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, AddActivitiesActivity.class);
          i.putExtra("projectid", projectsActivitiesData.get(groupPos).getProject().getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });
    } else { // normal activity item
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
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      holder.startDate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, DisplayActivitiesActivity.class);
          i.putExtra("activitiesid", activities.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      // handle click on the quick participation icon
      StyledButton quickParticipationBtn = (StyledButton) row.findViewById(R.id.quickParticipationBtn);
      quickParticipationBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          ParticipationDAO pDao = new ParticipationDAO(context);
          int largestParticipationId = pDao.getLargestParticipationId() + 1; //the new participation will have an id one more than the max so far recorded

          Intent i = new Intent(context, RecordQuickParticipationActivity.class);
          i.putExtra("largestParticipationId", largestParticipationId);
          i.putExtra("activitiesid", activities.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      // handle click on the edit activity icon
      StyledButton editActivityBtn = (StyledButton) row.findViewById(R.id.editActivityBtn);
      editActivityBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, EditActivitiesActivity.class);
          i.putExtra("activitiesid", activities.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      // handle click on the delete activity icon
      StyledButton deleteActivityBtn = (StyledButton) row.findViewById(R.id.deleteActivityBtn);
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