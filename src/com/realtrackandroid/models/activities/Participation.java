package com.realtrackandroid.models.activities;

import android.database.sqlite.SQLiteDatabase;

public class Participation {
  // Instance properties
  private int id; // used to modify an existing Activity. Set in ActivitiesDAO
  private int reminderid; // which reminder this participation is for. This is NOT a foreign key!!
  private int activityid; // which activity this participation is for. This is a foreign key that points to Activities.id
  private int men09; // number of men that participated
  private int men1017; // number of men that participated
  private int men1824; // number of men that participated
  private int menOver25; // number of men that participated
  private int women09; // number of women that participated
  private int women1017; // number of women that participated
  private int women1824; // number of women that participated
  private int womenOver25; // number of women that participated
  private long date; // date that the participation is for
  private boolean serviced; // true: participation was recorded. is only be set to true in RecordOrEditParticipationActivity
  private String notes;

  // Database table
  public static final String PARTICIPATION_TABLE = "participation";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_REMINDERID = "_reminderid";
  public static final String COLUMN_ACTIVITYID = "_activityid"; // foreign key referencing Activity.id
  public static final String COLUMN_UPDATED = "updated";
  public static final String COLUMN_MEN09 = "men09";
  public static final String COLUMN_MEN1017 = "men1017";
  public static final String COLUMN_MEN1824 = "men1824";
  public static final String COLUMN_MENOVER25 = "menOver25";
  public static final String COLUMN_WOMEN09 = "women09";
  public static final String COLUMN_WOMEN1017 = "women1017";
  public static final String COLUMN_WOMEN1824 = "women1824";
  public static final String COLUMN_WOMENOVER25 = "womenOver25";
  public static final String COLUMN_DATE = "date";
  public static final String COLUMN_ISSERVICED = "isserviced";
  public static final String COLUMN_NOTES = "notes";

  // Database creation SQL statement
  private static final String DATABASE_CREATE = "create table if not exists "
          + PARTICIPATION_TABLE
          + "("
          + COLUMN_ID + " integer primary key autoincrement, "
          + COLUMN_UPDATED + " integer not null default (strftime('%s','now')), "
          + COLUMN_MEN09 + " integer not null, "
          + COLUMN_MEN1017 + " integer not null, "
          + COLUMN_MEN1824 + " integer not null, "
          + COLUMN_MENOVER25 + " integer not null, "
          + COLUMN_WOMEN09 + " integer not null, "
          + COLUMN_WOMEN1017 + " integer not null, "
          + COLUMN_WOMEN1824 + " integer not null, "
          + COLUMN_WOMENOVER25 + " integer not null, "
          + COLUMN_ISSERVICED + " string not null, "
          + COLUMN_DATE + " integer not null, "
          + COLUMN_REMINDERID + " integer not null, "
          + COLUMN_NOTES + " text, "
          + COLUMN_ACTIVITYID + " integer not null references " + Activities.ACTIVITIES_TABLE + " (" + Activities.COLUMN_ID + ") ON DELETE CASCADE"
          + ");";

  // used to create the table
  public static void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  // used to upgrade the table
  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
          int newVersion) {
    database.execSQL("drop table if exists " + PARTICIPATION_TABLE);
    onCreate(database);
  }

  // Getters and Setters follow
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getReminderid() {
    return reminderid;
  }

  public void setReminderid(int reminderid) {
    this.reminderid = reminderid;
  }

  public long getDate() {
    return date;
  }

  public void setDate(long date) {
    this.date = date;
  }

  public boolean isServiced() {
    return serviced;
  }

  public void setServiced(boolean serviced) {
    this.serviced = serviced;
  }

  public int getActivityid() {
    return activityid;
  }

  public void setActivityid(int activityid) {
    this.activityid = activityid;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
  
  public int getMen09() {
    return men09;
  }
  
  public void setMen09(int men09) {
    this.men09 = men09;
  }
  
  public int getMen1017() {
    return men1017;
  }
  
  public void setMen1017(int men1017) {
    this.men1017 = men1017;
  }

  public int getMen1824() {
    return men1824;
  }

  public void setMen1824(int men1824) {
    this.men1824 = men1824;
  }

  public int getMenOver25() {
    return menOver25;
  }

  public void setMenOver25(int menOver25) {
    this.menOver25 = menOver25;
  }

  public int getWomen1824() {
    return women1824;
  }
  
  public int getWomen09() {
    return women09;
  }
  
  public void setWomen09(int women09) {
    this.women09 = women09;
  }
  
  public int getWomen1017() {
    return women1017;
  }
  
  public void setWomen1017(int women1017) {
    this.women1017 = women1017;
  }

  public void setWomen1824(int women1824) {
    this.women1824 = women1824;
  }

  public int getWomenOver25() {
    return womenOver25;
  }

  public void setWomenOver25(int womenOver25) {
    this.womenOver25 = womenOver25;
  }

  public int getTotalParticipants() {
    return men09 + men1017 + men1824 + menOver25 + women09 + women1017 + women1824 + womenOver25;
  }
}
