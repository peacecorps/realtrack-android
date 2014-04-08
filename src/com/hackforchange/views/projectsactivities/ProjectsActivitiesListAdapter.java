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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ActivitiesDAO;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.backend.projects.ProjectDAO;
import com.hackforchange.common.StyledButton;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.models.projects.Project;
import com.hackforchange.views.activities.AddActivitiesActivity;
import com.hackforchange.views.activities.DisplayActivitiesActivity;
import com.hackforchange.views.activities.EditActivitiesActivity;
import com.hackforchange.views.participationsactive.RecordQuickParticipationActivity;
import com.hackforchange.views.projects.AddProjectActivity;
import com.hackforchange.views.projects.DisplayProjectActivity;
import com.hackforchange.views.projects.EditProjectActivity;

public class ProjectsActivitiesListAdapter extends BaseExpandableListAdapter {
  private Context context;
  private int groupLayoutResourceId;
  private int childLayoutResourceId;
  private List<ProjectsActivitiesHolder> projectsActivitiesData = null;
  private View row;
  private LayoutInflater inflater;
  private ExpandableListView listView;
  private ParticipationDAO participationDAO;
  private DateFormat parser;

  public ProjectsActivitiesListAdapter(Context context, int groupLayoutResourceId, int childLayoutResourceId, ExpandableListView expandableListView, List<ProjectsActivitiesHolder> projectsActivitiesData) {
    super();
    this.groupLayoutResourceId = groupLayoutResourceId;
    this.childLayoutResourceId = childLayoutResourceId;
    this.context = context;
    listView = expandableListView;
    this.projectsActivitiesData = projectsActivitiesData;
    participationDAO = new ParticipationDAO(context);
    parser = new SimpleDateFormat("MM/dd/yyyy");
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
    return projectsActivitiesData.get(groupPosition).getActivitiesParticipationList().size();
  }

  @Override
  public Object getGroup(int groupPosition) {
    return projectsActivitiesData.get(groupPosition);
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    return projectsActivitiesData.get(groupPosition).getActivitiesParticipationList().get(childPosition);
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
    ParentViewHolder holder = null;

    if (row == null) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      row = inflater.inflate(groupLayoutResourceId, parent, false);

      holder = new ParentViewHolder();
      holder.projectTitle = (TextView) row.findViewById(R.id.projectTitle);
      holder.projectStartDate = (TextView) row.findViewById(R.id.projectStartDate);
      holder.editProjectBtn = (TextView) row.findViewById(R.id.editProjectBtn);
      holder.deleteProjectBtn = (TextView) row.findViewById(R.id.deleteProjectBtn);
      holder.expandCollapseProjectBtn = (StyledButton)row.findViewById(R.id.expandCollapseProjectBtn);

      row.setTag(holder);
    } else
      holder = (ParentViewHolder) row.getTag();

    final Project project = ((ProjectsActivitiesHolder) getGroup(groupPosition)).getProject();
    holder.projectTitle.setText(project.getTitle());

    if(project.getId() == -1){ //"add new project..." item.
      holder.editProjectBtn.setVisibility(View.INVISIBLE);
      holder.deleteProjectBtn.setVisibility(View.INVISIBLE);
      holder.projectStartDate.setVisibility(View.GONE);
      holder.expandCollapseProjectBtn.setText(context.getResources().getString(R.string.fa_plus));
      
      final ParentViewHolder holderFinal = holder;
      holder.expandCollapseProjectBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          holderFinal.projectTitle.performClick();
        }
      });

      holder.projectTitle.setOnClickListener(new View.OnClickListener() {
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
      holder.projectStartDate.setText(parser.format(startDate) + " to " + parser.format(endDate));

      // handle click on the title and show participation details
      holder.projectTitle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, DisplayProjectActivity.class);
          i.putExtra("projectid", project.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      holder.projectStartDate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, DisplayProjectActivity.class);
          i.putExtra("projectid", project.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      // expand and collapse groups
      final ParentViewHolder holderFinal = holder;
      holder.expandCollapseProjectBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(isExp){
            listView.collapseGroup(groupPos);
            holderFinal.expandCollapseProjectBtn.setText(context.getResources().getString(R.string.fa_rightchevron));
          }
          else{
            listView.expandGroup(groupPos);
            holderFinal.expandCollapseProjectBtn.setText(context.getResources().getString(R.string.fa_downchevron));
          }
        }
      });

      // handle click on the edit project icon
      holder.editProjectBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, EditProjectActivity.class);
          i.putExtra("projectid", project.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      // handle click on the delete project icon
      holder.deleteProjectBtn.setOnClickListener(new View.OnClickListener() {
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
    ChildViewHolder holder = null;

    if (row == null) {
      row = inflater.inflate(childLayoutResourceId, parent, false);

      holder = new ChildViewHolder();
      holder.activityTitle = (TextView) row.findViewById(R.id.activityTitle);
      holder.activityStartDate = (TextView) row.findViewById(R.id.activityStartDate);
      holder.editActivityBtn = (StyledButton) row.findViewById(R.id.editActivityBtn);
      holder.deleteActivityBtn = (StyledButton) row.findViewById(R.id.deleteActivityBtn);
      holder.quickParticipationBtn = (StyledButton) row.findViewById(R.id.quickParticipationBtn);
      holder.participationsLinearLayout = (LinearLayout) row.findViewById(R.id.participationsList);
      holder.expandCollapseActivityBtn = (StyledButton)row.findViewById(R.id.expandCollapseActivityBtn);
      holder.isExp = true; //show participationsLinearLayout by default

      row.setTag(holder);
    } else
      holder = (ChildViewHolder) row.getTag();

    final Activities activity = ((ActivitiesParticipationHolder) getChild(groupPosition, childPosition)).getActivity();
    holder.activityTitle.setText(activity.getTitle());

    if (activity.getId() == -1) { //"add new activity..." item.
      holder.activityStartDate.setVisibility(View.INVISIBLE);
      holder.editActivityBtn.setVisibility(View.INVISIBLE);
      holder.deleteActivityBtn.setVisibility(View.INVISIBLE);
      holder.quickParticipationBtn.setVisibility(View.INVISIBLE);
      holder.participationsLinearLayout.setVisibility(View.GONE);
      holder.expandCollapseActivityBtn.setText(context.getResources().getString(R.string.fa_plus));
      
      final ChildViewHolder holderFinal = holder;
      holder.expandCollapseActivityBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          holderFinal.activityTitle.performClick();
        }
      });

      holder.activityTitle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, AddActivitiesActivity.class);
          i.putExtra("projectid", projectsActivitiesData.get(groupPos).getProject().getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });
    } 
    else { // normal activity item
      Date startDate = new Date(activity.getStartDate());
      Date endDate = new Date(activity.getEndDate());
      holder.activityStartDate.setText(parser.format(startDate) + " to " + parser.format(endDate));

      // handle click on the title and show activity details
      holder.activityTitle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, DisplayActivitiesActivity.class);
          i.putExtra("activitiesid", activity.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      holder.activityStartDate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, DisplayActivitiesActivity.class);
          i.putExtra("activitiesid", activity.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      // handle click on the quick participation icon
      holder.quickParticipationBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          ParticipationDAO pDao = new ParticipationDAO(context);
          int largestParticipationId = pDao.getLargestParticipationId() + 1; //the new participation will have an id one more than the max so far recorded

          Intent i = new Intent(context, RecordQuickParticipationActivity.class);
          i.putExtra("largestParticipationId", largestParticipationId);
          i.putExtra("activitiesid", activity.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      // handle click on the edit activity icon
      holder.editActivityBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(context, EditActivitiesActivity.class);
          i.putExtra("activitiesid", activity.getId());
          context.startActivity(i);
          ((AllProjectsActivitiesActivity)context).overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        }
      });

      // handle click on the delete activity icon
      holder.deleteActivityBtn.setOnClickListener(new View.OnClickListener() {
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
              int activityId = activity.getId();
              aDao.deleteActivities(activityId);

              // make sure the list gets updated in the display
              projectsActivitiesData.get(groupPos).getActivitiesParticipationList().remove(childPos);
              notifyDataSetChanged();
            }
          })
          .show();
        }
      });
      
      /*---------------------------------------------------------------------------------
       * To display the participations, I explored and implemented a number of options
       * 1. An ExpandableListView as the child of this ExpandableListView.
       * 2. A ListView embedded in this child below the activity details.
       * Neither of these solutions play very well. First, because you're not supposed to
       * nest scrollable views, period. Second, we need to update the height of the children
       * on participation adds/deletes or activities adds/deletes or the children won't redraw
       * to fit in the new data. This is updating the height requires some jugglery with
       * onMeasure and MeasureSpec. It can be done but it feels like I'm not flowing
       * with the Android Tao.
       * 
       * Dynamically adding views is a much simpler solution. Plus, reading the participations
       * afresh every time does away with the PITA of managing the same backing data for nested
       * views.
       * Going with a programmatically added view *does* add some overhead in terms of simulating
       * "expanding" and "collapsing" of the activities but this is nowhere as nearly complex
       * as the other options.
       * -------------------------------------------------------------------------------*/
      holder.participationsLinearLayout.removeAllViews(); // required because child views are reused. So if you collapse a Project and re-expand it, it'll already
                                                          // have the old activity + participation information
      final List<Participation> participationList = participationDAO.getAllParticipationsForActivityId(activity.getId());
      
      if(participationList.isEmpty()){
        holder.isExp = false;
        holder.expandCollapseActivityBtn.setText(context.getResources().getString(R.string.fa_rightchevron));
      }
      
      for(final Participation p: participationList){
        // the false in the method call below ensures we don't attach the inflated layout right away. if we did, since all the
        // participationsLinearLayouts have the same id. the rest of this loop would only refer to the first participationLinearLayout
        final LinearLayout participationLinearLayout = (LinearLayout) inflater.inflate(R.layout.row_allparticipations, holder.participationsLinearLayout, false);
        Date d = new Date(p.getDate());
        
        // handle click on the title and show participation details
        final TextView participationDetails = (TextView) participationLinearLayout.findViewById(R.id.participationDetails);
        participationDetails.setText(parser.format(d)+":  "+p.getTotalParticipants()+" participants");
        participationDetails.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //TODO open participation details
          }
        });
        
        final Participation pFinal = p;
        
        StyledButton editParticipationBtn = (StyledButton) participationLinearLayout.findViewById(R.id.editParticipationBtn);
        editParticipationBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //TODO edit participation
          }
        });
        
        final ChildViewHolder finalHolder = holder;
        
        StyledButton deleteParticipationBtn = (StyledButton) participationLinearLayout.findViewById(R.id.deleteParticipationBtn);
        deleteParticipationBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            // warn the user first!
            new AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete this participation? This CANNOT be undone.")
            .setCancelable(false)
            .setNegativeButton("No", null)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                participationDAO.deleteParticipation(p.getId());
                finalHolder.participationsLinearLayout.removeView(participationLinearLayout);
              }
            })
            .show();
          }
        });
        
        holder.participationsLinearLayout.addView(participationLinearLayout);
      }
      
      // simulate expand collapse clicks for activities
      final ChildViewHolder holderFinal = holder;
      holder.expandCollapseActivityBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(holderFinal.isExp){ //"collapse"
            holderFinal.isExp = false;
            holderFinal.participationsLinearLayout.setVisibility(View.GONE);
            holderFinal.expandCollapseActivityBtn.setText(context.getResources().getString(R.string.fa_rightchevron));
          }
          else if(!participationList.isEmpty()){ //"expand". Only if there actually are participations
            holderFinal.isExp = true;
            holderFinal.participationsLinearLayout.setVisibility(View.VISIBLE);
            holderFinal.expandCollapseActivityBtn.setText(context.getResources().getString(R.string.fa_downchevron));
          }
        }
      });
    }
    
    return row;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return false; //must be true if you want child to be clickable!
  }

  private class ParentViewHolder {
    StyledButton expandCollapseProjectBtn;
    TextView deleteProjectBtn;
    TextView editProjectBtn;
    TextView projectTitle;
    TextView projectStartDate;
  }

  private class ChildViewHolder {
    LinearLayout participationsLinearLayout;
    boolean isExp;
    StyledButton expandCollapseActivityBtn;
    StyledButton editActivityBtn;
    StyledButton deleteActivityBtn;
    StyledButton quickParticipationBtn;
    TextView activityTitle;
    TextView activityStartDate;
  }
}

class ProjectsActivitiesHolder {
  Project project;
  List<ActivitiesParticipationHolder> activitiesParticipationList;

  Project getProject() {
    return project;
  }

  void setProject(Project project) {
    this.project = project;
  }

  List<ActivitiesParticipationHolder> getActivitiesParticipationList() {
    return activitiesParticipationList;
  }

  void setActivitiesParticipationList(List<ActivitiesParticipationHolder> activitiesParticipationList) {
    this.activitiesParticipationList = activitiesParticipationList;
  }
}

class ActivitiesParticipationHolder {
  Activities activity;
  List<Participation> participationList;
  
  public Activities getActivity() {
    return activity;
  }
  
  public void setActivity(Activities activity) {
    this.activity = activity;
  }
  
  public List<Participation> getParticipationList() {
    return participationList;
  }
  
  public void setParticipationList(List<Participation> participationList) {
    this.participationList = participationList;
  }
}
