package com.hackforchange.models.activities;

import android.database.sqlite.SQLiteDatabase;

/**
 * ********************************************************************************************************************
 * Models the representation of an activity
 * ********************************************************************************************************************
 */
public class Participation {
    // Instance properties
    private int id; // used to modify an existing Activity. Set in ActivitiesDAO
    private int reminderid; // which reminder this participation is for. This is NOT a foreign key!!
    private int activityid; // which activity this participation is for. This is a foreign key that points to Activities.id
    private int men; // number of men that participated
    private int women; // number of women that participated
    private long date; // date that the participation is for
    private boolean serviced; // true: participation was recorded. is only be set to true in RecordParticipationActivity

    // Database table
    public static final String PARTICIPATION_TABLE = "participation";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_REMINDERID = "_reminderid"; // foreign key referencing Reminders.id
    public static final String COLUMN_ACTIVITYID = "_activityid"; // foreign key referencing Reminders.id
    public static final String COLUMN_UPDATED = "updated"; //when this PARTICIPATION was last modified
    public static final String COLUMN_MEN = "men";
    public static final String COLUMN_WOMEN = "women";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ISSERVICED = "isserviced";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + PARTICIPATION_TABLE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_UPDATED + " integer not null default (strftime('%s','now')), "
            + COLUMN_MEN + " integer not null, "
            + COLUMN_WOMEN + " integer not null, "
            + COLUMN_ISSERVICED + " string not null, "
            + COLUMN_DATE + " integer not null, "
            + COLUMN_REMINDERID + " integer not null, "
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

    public int getMen() {
        return men;
    }

    public void setMen(int men) {
        this.men = men;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getWomen() {
        return women;
    }

    public void setWomen(int women) {
        this.women = women;
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
}
