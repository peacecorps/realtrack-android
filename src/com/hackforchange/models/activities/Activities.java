package com.hackforchange.models.activities;

import android.database.sqlite.SQLiteDatabase;
import com.hackforchange.models.projects.Project;

/***********************************************************************************************************************
 * Models the representation of an activity
 **********************************************************************************************************************/
public class Activities {
  // Instance properties
  private int id; // used to modify an existing Activity. Set in ActivitiesDAO
  private int projectid; // which project this Activities is a part of. This is a foreign key that points to Project.id
  private String title;
  private long startDate;
  private long endDate;
  private String notes; // notes are optional
  private String orgs; // organizations associated with this activity; optional
  private String comms; // communities associated with this activity; optional
  private String initiatives; // initiatives associated with this activity; optional
  // TODO: Add reminders. Maybe need a separate table for this?

  // Database table
  public static final String ACTIVITIES_TABLE = "ACTIVITIES";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_PROJECTID = "_projectid"; // foreign key referencing Project.id
  public static final String COLUMN_UPDATED = "updated"; //when this ACTIVITIES was last modified
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_STARTDATE = "startdate";
  public static final String COLUMN_ENDDATE = "enddate";
  public static final String COLUMN_NOTES = "notes";
  public static final String COLUMN_ORGS = "orgs";
  public static final String COLUMN_COMMS = "comms";
  public static final String COLUMN_INITIATIVES = "initiatives";

  // Database creation SQL statement
  private static final String DATABASE_CREATE = "create table if not exists "
    + ACTIVITIES_TABLE
    + "("
    + COLUMN_ID + " integer primary key autoincrement, "
    + COLUMN_UPDATED + " integer not null default (strftime('%s','now')), "
    + COLUMN_TITLE + " text not null, "
    + COLUMN_NOTES + " text, " // notes are optional
    + COLUMN_ORGS + " text, " // notes are optional
    + COLUMN_COMMS + " text, " // notes are optional
    + COLUMN_INITIATIVES + " text, " // notes are optional
    + COLUMN_STARTDATE    + " integer not null, "
    + COLUMN_ENDDATE    + " integer not null, "
    + COLUMN_PROJECTID + " integer not null references " + Project.PROJECT_TABLE + " (" + Project.COLUMN_ID + ") ON DELETE CASCADE "
                             //foreign key constraint. Make sure to delete the activities if the project that owns it is deleted
    + ");";

  // used to create the table
  public static void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  // used to upgrade the table
  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                               int newVersion) {
    database.execSQL("drop table if exists " + ACTIVITIES_TABLE);
    onCreate(database);
  }

  // Getters and Setters follow
  public int getProjectid() {
    return projectid;
  }

  public void setProjectid(int projectid) {
    this.projectid = projectid;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Activities(){
    super();
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public long getStartDate() {
    return startDate;
  }

  public void setStartDate(long startDate) {
    this.startDate = startDate;
  }

  public long getEndDate() {
    return endDate;
  }

  public void setEndDate(long endDate) {
    this.endDate = endDate;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getOrgs() {
    return orgs;
  }

  public void setOrgs(String orgs) {
    this.orgs = orgs;
  }

  public String getComms() {
    return comms;
  }

  public void setComms(String comms) {
    this.comms = comms;
  }

  public String getInitiatives() {
    return initiatives;
  }

  public void setInitiatives(String initiatives) {
    this.initiatives = initiatives;
  }

}
