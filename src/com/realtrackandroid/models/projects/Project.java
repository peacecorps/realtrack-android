package com.realtrackandroid.models.projects;

import android.database.sqlite.SQLiteDatabase;

/**
 * ********************************************************************************************************************
 * Models the representation of a project
 * ********************************************************************************************************************
 */
public class Project {
    // Instance properties
    private int id; // used to modify an existing Project. Set in ProjectDAO
    private String title;
    private long startDate;
    private long endDate;
    private String notes; // notes are optional

    // Database table
    public static final String PROJECT_TABLE = "projects";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UPDATED = "updated";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_STARTDATE = "startdate";
    public static final String COLUMN_ENDDATE = "enddate";
    public static final String COLUMN_NOTES = "notes";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table if not exists "
        + PROJECT_TABLE
        + "("
        + COLUMN_ID + " integer primary key autoincrement, "
        + COLUMN_UPDATED + " integer not null default (strftime('%s','now')), "
        + COLUMN_TITLE + " text not null, "
        + COLUMN_NOTES + " text, " // notes are optional
        + COLUMN_STARTDATE + " integer not null, "
        + COLUMN_ENDDATE + " integer not null"
        + ");";

    // used to create the table
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    // used to upgrade the table
    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("drop table if exists " + PROJECT_TABLE);
        onCreate(database);
    }

    // Getters and Setters follow
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Project() {
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
}
