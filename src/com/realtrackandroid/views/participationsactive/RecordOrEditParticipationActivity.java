package com.realtrackandroid.views.participationsactive;

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
import com.realtrackandroid.backend.activities.ParticipationDAO;
import com.realtrackandroid.models.activities.Participation;
import com.realtrackandroid.views.help.FrameworkInfoDialog;
import com.realtrackandroid.views.help.GlossaryDialog;
import com.realtrackandroid.views.help.HelpDialog;

/**
 * RecordOrEditParticipationActivity is different from RecordQuickParticipationActivity in the
 * following ways: 1. RecordOrEditParticipationActivity ALREADY has a participation associated with
 * it (created when reminders are served (in NotificationService)). RecordQuickParticipationActivity
 * does not have a pre-existing participation. It has to create one. 2. Because
 * RecordOrEditParticipationActivity serves an existing participation, it has a date and time
 * associated with it. RecordQuickParticipationActivity does not have a date and time a priori, and
 * must get these from the user.
 * 
 * @author Raj
 */
public class RecordOrEditParticipationActivity extends SherlockFragmentActivity implements
        RecordOrEditParticipationFragmentInterface {
  static final int ADD_PARTICIPANTS_REQUEST = 1;

  private int participationId;

  protected Participation p;

  private boolean editParticipation; // true if coming in from ProjectsActivitiesListAdapter

  private ParticipationPageAdapter pageAdapter;

  List<Fragment> fragments;

  private PagerSlidingTabStrip tabs;

  private List<String> fragmentTitles;

  private OptionalFragmentRecordOrEditParticipation optionalFragment;

  private RequiredFragmentRecordOrEditParticipation requiredFragment;

  private ParticipationDAO participationDao;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.base_pager);

    fragments = createFragments();
    requiredFragment = (RequiredFragmentRecordOrEditParticipation) fragments.get(0);
    optionalFragment = (OptionalFragmentRecordOrEditParticipation) fragments.get(1);

    tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
    pageAdapter = new ParticipationPageAdapter(getSupportFragmentManager(), fragments);

    ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
    pager.setAdapter(pageAdapter);

    tabs.setViewPager(pager);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // read in the ID of the activities for which we're recording participation
    participationId = getIntent().getExtras().getInt("participationid");
    participationDao = new ParticipationDAO(getApplicationContext());
    p = participationDao.getParticipationWithId(participationId);

    // also note the date and time
    requiredFragment.setDateTime(getIntent().getExtras().getLong("datetime"));

    editParticipation = getIntent().getExtras().getBoolean("editparticipation");
    requiredFragment.setEditParticipation(editParticipation);
    optionalFragment.setEditParticipation(editParticipation);

    if (editParticipation) {
      this.setTitle(getResources().getString(R.string.editparticipationactivity_label));
    }
  }

  private List<Fragment> createFragments() {
    fragmentTitles = new ArrayList<String>();
    fragmentTitles.add("Required");
    fragmentTitles.add("Optional");

    List<Fragment> fList = new ArrayList<Fragment>();
    fList.add(RequiredFragmentRecordOrEditParticipation.newInstance(fragmentTitles.get(0)));
    fList.add(OptionalFragmentRecordOrEditParticipation.newInstance(fragmentTitles.get(1)));

    return fList;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.recordoreditparticipationmenu, menu);

    if (editParticipation)
      menu.findItem(R.id.action_dismiss).setVisible(false);

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
        GlossaryDialog glossaryDialog = new GlossaryDialog();
        glossaryDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        glossaryDialog.show(getSupportFragmentManager(), "glossarydialog");
        break;
      case R.id.action_save:
        saveParticipation();
        break;
      case R.id.action_dismiss:
        dismissParticipation();
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  private void dismissParticipation() {
    participationDao.deleteParticipation(participationId);
    finish();
  }

  private void saveParticipation() {
    if (!requiredFragment.setFields(p))
      return;

    optionalFragment.setFields(p);

    // update the serviced flag for this Reminder in the Reminders table
    // so that the next time the NotificationReceiver checks, this participation
    // does not show up as unserviced
    p.setServiced(true);

    participationDao.updateParticipation(p);

    finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.animation_slideinleft, R.anim.animation_slideoutright);
    finish();
  }

  private class ParticipationPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public ParticipationPageAdapter(FragmentManager fm, List<Fragment> fragments) {
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
  public Participation getParticipation() {
    return p;
  }
}