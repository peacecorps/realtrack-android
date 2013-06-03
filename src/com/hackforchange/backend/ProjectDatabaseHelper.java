package com.hackforchange.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.hackforchange.models.Project;

public class ProjectDatabaseHelper extends SQLiteOpenHelper {
  public static final String DATABASE_NAME = "project.db";
  public static final int DATABASE_VERSION = 1;

  public ProjectDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // Method is called during creation of the database
  @Override
  public void onCreate(SQLiteDatabase database) {
    Project.onCreate(database);
  }

  // Method is called during an upgrade of the database,
  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion,
                        int newVersion) {
    Project.onUpgrade(database, oldVersion, newVersion);
  }
}
