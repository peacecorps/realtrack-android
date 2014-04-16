package com.hackforchange.views.participationsdonesummaries;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hackforchange.R;
import com.hackforchange.common.StyledButton;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.models.projects.Project;
import com.hackforchange.views.participationsdonesummaries.ParticipationSummaryActivity.ActivityHolder;
import com.hackforchange.views.participationsdonesummaries.ParticipationSummaryActivity.ParticipationHolder;
import com.hackforchange.views.participationsdonesummaries.ParticipationSummaryActivity.ProjectHolder;

public class ParticipationSummaryListAdapter extends BaseExpandableListAdapter {
  private Context context;
  private int groupLayoutResourceId;
  private int childLayoutResourceId;
  private List<ProjectHolder> projectHolderData = null;
  private View row;
  private LayoutInflater inflater;
  private ExpandableListView listView;
  
  private XYSeries mCurrentSeries;
  private XYMultipleSeriesDataset mDataset;
  private GraphicalView mChartView;
  private XYMultipleSeriesRenderer mRenderer;
  private Calendar activityCal, participationCal;

  public ParticipationSummaryListAdapter(Context context, int groupLayoutResourceId, int childLayoutResourceId, ExpandableListView expandableListView, List<ProjectHolder> projectHolderData) {
    super();
    this.groupLayoutResourceId = groupLayoutResourceId;
    this.childLayoutResourceId = childLayoutResourceId;
    this.context = context;
    listView = expandableListView;
    this.projectHolderData = projectHolderData;
    participationCal = Calendar.getInstance();
    activityCal = Calendar.getInstance();
  }

  public void setInflater(LayoutInflater inflater) {
    this.inflater = inflater;
  }

  @Override
  public int getGroupCount() {
    return projectHolderData.size();
  }

  @Override
  public int getChildrenCount(int groupPosition) {
    return projectHolderData.get(groupPosition).activityHolderList.size();
  }

  @Override
  public Object getGroup(int groupPosition) {
    return projectHolderData.get(groupPosition);
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    return projectHolderData.get(groupPosition).activityHolderList.get(childPosition);
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
      holder.projectSummaryLayout = (LinearLayout) row.findViewById(R.id.projectSummaryLayout);
      holder.projectTitle = (TextView) row.findViewById(R.id.projectTitle);
      holder.expandCollapseProjectBtn = (StyledButton)row.findViewById(R.id.expandCollapseProjectBtn);

      row.setTag(holder);
    } else
      holder = (ParentViewHolder) row.getTag();

    ProjectHolder pHolder = (ProjectHolder) getGroup(groupPosition);
    final Project project = pHolder.p;
    holder.projectTitle.setText(project.getTitle());
    
    // hide this group if it has no participations
    boolean hasParticipations = false;
    
    for(ActivityHolder aHolder: pHolder.activityHolderList){
      if(!aHolder.participationHolderList.isEmpty()){
        hasParticipations = true;
        break;
      }
    }
    
    if(!hasParticipations){
      holder.projectSummaryLayout.removeAllViews();
      return row;
    }

    // make sure these views show or else scrolling down all the way and then scrolling up screws them up
    if(isExp)
      holder.expandCollapseProjectBtn.setText(context.getResources().getString(R.string.fa_downchevron));
    else
      holder.expandCollapseProjectBtn.setText(context.getResources().getString(R.string.fa_rightchevron));

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

    return row;
  }

  @Override
  public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    row = convertView;
    ChildViewHolder holder = null;

    if (row == null) {
      row = inflater.inflate(childLayoutResourceId, parent, false);

      holder = new ChildViewHolder();
      holder.activitySummaryLayout = (LinearLayout) row.findViewById(R.id.activitySummaryLayout);
      holder.activityTitle = (TextView) row.findViewById(R.id.activityTitle);
      holder.numEvents = (TextView) row.findViewById(R.id.numEvents);
      holder.totalParticipants = (TextView) row.findViewById(R.id.totalParticipants);
      holder.activityGraphLinearLayout = (LinearLayout) row.findViewById(R.id.activityGraphLinearLayout);
      holder.expandCollapseActivityBtn = (StyledButton)row.findViewById(R.id.expandCollapseActivityBtn);
      holder.isExp = true;

      row.setTag(holder);
    } else
      holder = (ChildViewHolder) row.getTag();

    ActivityHolder aHolder = (ActivityHolder) getChild(groupPosition, childPosition);

    holder.activityGraphLinearLayout.removeAllViews(); // required because views are reused.
    final List<ParticipationHolder> participationHolderList =  aHolder.participationHolderList;

    if(participationHolderList.isEmpty()){
      holder.activitySummaryLayout.removeAllViews();
      return row;
      //collapseChild(holder);
    }

    int sumParticipants = 0;
    mCurrentSeries = new XYSeries("");

    final Activities a = aHolder.a;
    activityCal.setTimeInMillis(a.getStartDate());
    mCurrentSeries.add(0, 0);
    activityCal.add(Calendar.DAY_OF_WEEK, 7);
    int weekNum = 1;

    // sort the participation list first because there's no guarantee participations were added
    // to the database in the order of their dates e.g. a quick add participation could easily
    // be out of order
    Collections.sort(aHolder.participationHolderList, new Comparator<ParticipationHolder>(){
      @Override
      public int compare(ParticipationHolder pa1, ParticipationHolder pa2) {
        long date1 = pa1.pa.getDate();
        long date2 = pa2.pa.getDate();
        if(date1 == date2)
          return 0;
        else if(date1 > date2)
          return 1;
        else
          return -1;
      }
    });

    for (int i=0; i<aHolder.participationHolderList.size(); ++i) {
      Participation participation = aHolder.participationHolderList.get(i).pa;
      
      participationCal.setTimeInMillis(participation.getDate());
      if(participationCal.before(activityCal)){
        sumParticipants += participation.getTotalParticipants();
        if(weekNum==mCurrentSeries.getMaxX())
          mCurrentSeries.remove(mCurrentSeries.getItemCount()-1);
        mCurrentSeries.add(weekNum, sumParticipants);
      } else {
        //sumParticipants += participation.getTotalParticipants();
        //mCurrentSeries.add(++weekNum, sumParticipants);
        activityCal.add(Calendar.DAY_OF_WEEK, 7);
        weekNum++;
        i--;
      }
    }

    holder.activityTitle.setText(a.getTitle());
    holder.numEvents.setText("["+aHolder.participationHolderList.size()+"] event(s)");
    holder.totalParticipants.setText("["+sumParticipants+"] participants");

    createGraph(row, mCurrentSeries);

    // simulate expand collapse clicks for activities
    final ChildViewHolder holderFinal = holder;
    holder.expandCollapseActivityBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(holderFinal.isExp){ //"collapse"
          collapseChild(holderFinal);
          holderFinal.activityGraphLinearLayout.setVisibility(View.GONE);
        }
        else if(!participationHolderList.isEmpty()){ //"expand". Only if there actually are participations
          expandChild(holderFinal);
          holderFinal.activityGraphLinearLayout.setVisibility(View.VISIBLE);
        }
      }
    });

    return row;
  }
  
  private void createGraph(View view, XYSeries mCurrentSeries) {
    mRenderer = getMultipleSeriesRenderer();
    mRenderer.addSeriesRenderer(getSeriesRenderer());
    mDataset = getMultipleSeriesDataset(mCurrentSeries);
    mChartView = ChartFactory.getLineChartView(context, mDataset, mRenderer);
    LinearLayout graphView = (LinearLayout) view.findViewById(R.id.activityGraphLinearLayout);
    graphView.addView(mChartView);
  }
  
  private XYMultipleSeriesDataset getMultipleSeriesDataset(XYSeries mCurrentSeries) {
    mDataset = new XYMultipleSeriesDataset();
    mDataset.addSeries(mCurrentSeries);
    return mDataset;
  }

  private XYMultipleSeriesRenderer getMultipleSeriesRenderer() {
    mRenderer = new XYMultipleSeriesRenderer();
    mRenderer.setClickEnabled(false);
    mRenderer.setShowLegend(false);
    mRenderer.setAntialiasing(true);
    mRenderer.setAxisTitleTextSize(15f);
    mRenderer.setXLabelsColor(context.getResources().getColor(R.color.darkgrey));
    mRenderer.setYLabelsColor(0, context.getResources().getColor(R.color.darkgrey));
    mRenderer.setLabelsTextSize(15f);
    mRenderer.setXLabelsPadding(2f);
    mRenderer.setYLabelsPadding(10f);
    mRenderer.setXTitle("Weeks");
    mRenderer.setYTitle("Participants");
    mRenderer.setYLabels(3);

    mRenderer.setInScroll(true);

    mRenderer.setMargins(new int[]{25, 45, 25, 25});
    mRenderer.setMarginsColor(context.getResources().getColor(R.color.white));

    mRenderer.setZoomEnabled(false);
    mRenderer.setPanEnabled(false);
    return mRenderer;
  }

  private XYSeriesRenderer getSeriesRenderer() {
    XYSeriesRenderer renderer = new XYSeriesRenderer();
    renderer.setFillPoints(false);
    renderer.setDisplayChartValues(false);
    renderer.setColor(context.getResources().getColor(R.color.blue));
    
    FillOutsideLine fill = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ALL);
    fill.setColor(context.getResources().getColor(R.color.blue));
    renderer.addFillOutsideLine(fill);
    
    return renderer;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return false; //must be true if you want child to be clickable!
  }

  private void expandChild(ChildViewHolder holder) {
    holder.isExp = true;
    holder.expandCollapseActivityBtn.setText(context.getResources().getString(R.string.fa_downchevron));
  }

  private void collapseChild(ChildViewHolder holder) {
    holder.isExp = false;
    holder.expandCollapseActivityBtn.setText(context.getResources().getString(R.string.fa_rightchevron));
  }

  private class ParentViewHolder {
    LinearLayout projectSummaryLayout;
    StyledButton expandCollapseProjectBtn;
    TextView projectTitle;
  }

  private class ChildViewHolder {
    LinearLayout activitySummaryLayout;
    LinearLayout activityGraphLinearLayout;
    boolean isExp;
    StyledButton expandCollapseActivityBtn;
    TextView activityTitle;
    TextView numEvents;
    TextView totalParticipants;
  }
}
