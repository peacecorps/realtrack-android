package com.hackforchange.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hackforchange.R;
import com.hackforchange.models.activities.Activities;
import com.hackforchange.models.activities.Participant;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.models.projects.Project;
import com.hackforchange.models.reminders.Reminders;

/*
 * This is a database helper that is shared by the different model classes
 */
public class GlobalDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "database.db";
    public static final int DATABASE_VERSION = 2;
    private static GlobalDatabaseHelper gHelper;

    private GlobalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static GlobalDatabaseHelper getInstance(Context context) {
        gHelper = new GlobalDatabaseHelper(context);
        return gHelper;
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        Project.onCreate(database);
        Activities.onCreate(database);
        Reminders.onCreate(database);
        Participation.onCreate(database);
        Participant.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Participant.onUpgrade(database, oldVersion, newVersion);
    }
}
