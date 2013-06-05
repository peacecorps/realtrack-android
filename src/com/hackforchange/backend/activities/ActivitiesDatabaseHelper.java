package com.hackforchange.backend.activities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.hackforchange.models.activities.Activities;

public class ActivitiesDatabaseHelper extends SQLiteOpenHelper {
  public static final String DATABASE_NAME = "activities.db";
  public static final int DATABASE_VERSION = 1;

  public ActivitiesDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // Method is called during creation of the database
  @Override
  public void onCreate(SQLiteDatabase database) {
    Activities.onCreate(database);
  }

  // Method is called during an upgrade of the database,
  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion,
                        int newVersion) {
    Activities.onUpgrade(database, oldVersion, newVersion);
  }
}
