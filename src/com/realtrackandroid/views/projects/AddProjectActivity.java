package com.realtrackandroid.views.projects;

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
import com.realtrackandroid.backend.projects.ProjectDAO;
import com.realtrackandroid.models.projects.Project;
import com.realtrackandroid.views.dialogs.PickDateDialogListener;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.HelpDialog;

/*
 * Presents an activity that lets you add a new project
 * Pressing the back key will exit the activity without adding a project
 */
public class AddProjectActivity extends SherlockFragmentActivity implements PickDateDialogListener,
        ProjectFragmentInterface {
  protected Project p;

  protected OptionalFragment optionalFragment;

  protected RequiredFragment requiredFragment;

  private ProjectPageAdapter pageAdapter;

  List<Fragment> fragments;

  private PagerSlidingTabStrip tabs;

  private List<String> fragmentTitles;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.base_pager);

    fragments = createFragments();
    requiredFragment = (RequiredFragment) fragments.get(0);
    optionalFragment = (OptionalFragment) fragments.get(1);

    tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
    pageAdapter = new ProjectPageAdapter(getSupportFragmentManager(), fragments);

    ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
    pager.setAdapter(pageAdapter);

    tabs.setViewPager(pager);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  private List<Fragment> createFragments() {
    fragmentTitles = new ArrayList<String>();
    fragmentTitles.add("Required");
    fragmentTitles.add("Optional");

    List<Fragment> fList = new ArrayList<Fragment>();
    fList.add(RequiredFragment.newInstance(fragmentTitles.get(0)));
    fList.add(OptionalFragment.newInstance(fragmentTitles.get(1)));

    return fList;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.addprojectmenu, menu);

    getSupportActionBar().setDisplayShowTitleEnabled(true);
    return true;
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
        saveProject();
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  private void saveProject() {
    p = new Project();

    if (!requiredFragment.setFields(p))
      return;

    optionalFragment.setFields(p);

    ProjectDAO pDao = new ProjectDAO(getApplicationContext());
    pDao.addProject(p);
    finish();
  }

  @Override
  public void setDate(String date) {
    requiredFragment.setDate(date);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }

  private class ProjectPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public ProjectPageAdapter(FragmentManager fm, List<Fragment> fragments) {
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

}