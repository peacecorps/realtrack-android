package com.realtrackandroid.views.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.astuetz.PagerSlidingTabStrip;
import com.realtrackandroid.R;
import com.realtrackandroid.backend.activities.ActivitiesDAO;
import com.realtrackandroid.backend.projects.ProjectDAO;
import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.projects.Project;
import com.realtrackandroid.views.dialogs.PickDateDialogListener;
import com.realtrackandroid.views.dialogs.PickTimeDialogListener;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.HelpDialog;

/**
 * Add a new activity to an existing project
 */
public class AddActivitiesActivity extends SherlockFragmentActivity implements PickDateDialogListener, PickTimeDialogListener, ActivitiesFragmentInterface {
  protected int projectid;
  protected RequiredFragment requiredFragment;
  protected RemindersFragment remindersFragment;
  protected OptionalFragment optionalFragment;
  protected Activities a;
  protected Project p;
  private ActivitiesPageAdapter pageAdapter;
  List<Fragment> fragments;
  private PagerSlidingTabStrip tabs;
  private List<String> fragmentTitles;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.base_pager);
    
    fragments = createFragments();
    requiredFragment = (RequiredFragment) fragments.get(0);
    optionalFragment = (OptionalFragment) fragments.get(1);
    remindersFragment = (RemindersFragment) fragments.get(2);
    
    tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
    pageAdapter = new ActivitiesPageAdapter(getSupportFragmentManager(), fragments);
    
    ViewPager pager = (ViewPager)findViewById(R.id.viewpager);
    pager.setAdapter(pageAdapter);
    
    tabs.setViewPager(pager);
    
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
    // get the owner project
    projectid = getIntent().getExtras().getInt("projectid");
    ProjectDAO pDao = new ProjectDAO(getApplicationContext());
    p = pDao.getProjectWithId(projectid);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // provide a back button on the actionbar
        finish();
        break;
      case R.id.action_help:
        HelpDialog helpDialog = new HelpDialog();
        helpDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        helpDialog.show(getSupportFragmentManager(), "helpdialog");
        break;
      case R.id.action_framework:
        FrameworkInfoDialog frameworkInfoDialog = new FrameworkInfoDialog();
        frameworkInfoDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        frameworkInfoDialog.show(getSupportFragmentManager(), "frameworkinfodialog");
        break;
      case R.id.action_glossary:
        HelpDialog glossaryDialog = new HelpDialog();
        glossaryDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        glossaryDialog.setDisplayUrl("file:///android_asset/glossary.html");
        glossaryDialog.show(getSupportFragmentManager(), "glossarydialog");
        break;
      case R.id.action_save:
        saveActivity();
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  private void saveActivity() {
    a = new Activities();
    
    if(!requiredFragment.setFields(a))
      return;

    optionalFragment.setFields(a);
    
    a.setProjectid(projectid);

    ActivitiesDAO aDao = new ActivitiesDAO(getApplicationContext());

    // createdActivityId is the ID of the activity we just created
    int createdActivityId = aDao.addActivities(a);
    
    remindersFragment.setFields(a, createdActivityId);
    
    finish();

  }

  @Override
  public void setDate(String date) {
    requiredFragment.setDate(date);
  }

  @Override
  public void setTime(String time) {
    remindersFragment.setTime(time);
  }
  
  private List<Fragment> createFragments(){
    fragmentTitles = new ArrayList<String>();
    fragmentTitles.add("Required");
    fragmentTitles.add("Optional");
    fragmentTitles.add("Reminders");
    
    List<Fragment> fList = new ArrayList<Fragment>();
    fList.add(RequiredFragment.newInstance(fragmentTitles.get(0)));
    fList.add(OptionalFragment.newInstance(fragmentTitles.get(1)));
    fList.add(RemindersFragment.newInstance(fragmentTitles.get(2)));
    
    return fList;
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.addactivitymenu, menu);

    getSupportActionBar().setDisplayShowTitleEnabled(true);
    return true;
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }
  
  private class ActivitiesPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

      public ActivitiesPageAdapter(FragmentManager fm, List<Fragment> fragments) {
          super(fm);
          this.fragments = fragments;
      }
      
      @Override
      public Fragment getItem(int position) {
          return this.fragments.get(position);
      }
      
      @Override
      public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
      }
   
      @Override
      public int getCount() {
          return this.fragments.size();
      }
  }

  @Override
  public Project getProject() {
    return p;
  }

  @Override
  public Activities getActivities() {
    return a;
  }
}