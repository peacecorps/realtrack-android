package com.hackforchange.backend.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.hackforchange.backend.GlobalDatabaseHelper;
import com.hackforchange.models.activities.Activities;

import java.util.ArrayList;

/*
 * DAO object to update/delete/add activities
 */
public class ActivitiesDAO {
  private GlobalDatabaseHelper opener;
  private SQLiteDatabase readDatabase;
  private SQLiteDatabase writeDatabase;

  public ActivitiesDAO(Context context) {
    this.opener = GlobalDatabaseHelper.getInstance(context);
    this.readDatabase = opener.getReadableDatabase();
    this.writeDatabase = opener.getWritableDatabase();
    this.writeDatabase.execSQL("PRAGMA foreign_keys=ON"); // make sure to turn foreign keys constraints on
  }

  public ArrayList<Activities> getAllActivitiesForProjectId(int projectid) {
    ArrayList<Activities> output = null;
    String[] columnsToRead = new String[8];
    columnsToRead[0] = Activities.COLUMN_TITLE;
    columnsToRead[1] = Activities.COLUMN_STARTDATE;
    columnsToRead[2] = Activities.COLUMN_ENDDATE;
    columnsToRead[3] = Activities.COLUMN_NOTES;
    columnsToRead[4] = Activities.COLUMN_ID;
    columnsToRead[5] = Activities.COLUMN_ORGS;
    columnsToRead[6] = Activities.COLUMN_COMMS;
    columnsToRead[7] = Activities.COLUMN_INITIATIVES;
    String whereClause = Activities.COLUMN_PROJECTID + '=' + projectid;
    Cursor returnData = readDatabase.query(Activities.ACTIVITIES_TABLE, columnsToRead,
      whereClause, null, null, null, null);
    output = extractActivities(returnData);
    return output;
  }

  private ArrayList<Activities> extractActivities(Cursor returnData) {
    // The output ArrayList is initialized
    ArrayList<Activities> output = new ArrayList<Activities>();
    // Move the counter to the first item in the return data
    returnData.moveToFirst();
    int count = 0;
    // While there are still values in the return data
    while (!returnData.isAfterLast()) {
      // Add the new Activities to the ArrayList
      Activities a = new Activities();
      a.setTitle(returnData.getString(0));
      a.setStartDate(returnData.getLong(1));
      a.setEndDate(returnData.getLong(2));
      a.setNotes(returnData.getString(3));
      a.setId(Integer.parseInt(returnData.getString(4)));
      a.setOrgs(returnData.getString(5));
      a.setComms(returnData.getString(6));
      a.setInitiatives(returnData.getString(7));
      output.add(count, a);
      // Advance the Cursor
      returnData.moveToNext();
      // Advance the counter
      count++;
    }
    // Return the ArrayList
    return output;
  }

  public Activities getActivityWithId(int id) {
    String[] columnsToRead = new String[9];
    columnsToRead[0] = Activities.COLUMN_TITLE;
    columnsToRead[1] = Activities.COLUMN_STARTDATE;
    columnsToRead[2] = Activities.COLUMN_ENDDATE;
    columnsToRead[3] = Activities.COLUMN_NOTES;
    columnsToRead[4] = Activities.COLUMN_ID;
    columnsToRead[5] = Activities.COLUMN_ORGS;
    columnsToRead[6] = Activities.COLUMN_COMMS;
    columnsToRead[7] = Activities.COLUMN_INITIATIVES;
    columnsToRead[8] = Activities.COLUMN_PROJECTID;
    String whereClause = Activities.COLUMN_ID + '=' + id;
    Cursor returnData = readDatabase.query(Activities.ACTIVITIES_TABLE, columnsToRead,
      whereClause, null, null, null, null);
    returnData.moveToFirst();
    Activities a = new Activities();
    a.setTitle(returnData.getString(0));
    a.setStartDate(returnData.getLong(1));
    a.setEndDate(returnData.getLong(2));
    a.setNotes(returnData.getString(3));
    a.setId(Integer.parseInt(returnData.getString(4)));
    a.setOrgs(returnData.getString(5));
    a.setComms(returnData.getString(6));
    a.setInitiatives(returnData.getString(7));
    a.setProjectid(returnData.getInt(8));
    // Return the constructed Activities object
    return a;
  }

  public int addActivities(Activities activity) {
    ContentValues newValue = new ContentValues(8);
    newValue.put(Activities.COLUMN_TITLE, activity.getTitle());
    newValue.put(Activities.COLUMN_STARTDATE, activity.getStartDate());
    newValue.put(Activities.COLUMN_ENDDATE, activity.getEndDate());
    newValue.put(Activities.COLUMN_NOTES, activity.getNotes());
    newValue.put(Activities.COLUMN_ORGS, activity.getOrgs());
    newValue.put(Activities.COLUMN_COMMS, activity.getComms());
    newValue.put(Activities.COLUMN_INITIATIVES, activity.getInitiatives());
    newValue.put(Activities.COLUMN_PROJECTID, activity.getProjectid());

    // Insert the item into the database
    writeDatabase.insert(Activities.ACTIVITIES_TABLE, null, newValue);

    // return the id of the activity just created. This will be used as the foreign key for the reminders table
    Cursor returnData = readDatabase.rawQuery("select seq from sqlite_sequence where name=?", new String[]{Activities.ACTIVITIES_TABLE});
    if (returnData != null && returnData.moveToFirst())
      return returnData.getInt(0);
    else
      return -1;
  }

  public void updateActivities(Activities activity) {
    ContentValues newValue = new ContentValues(8);
    newValue.put(Activities.COLUMN_TITLE, activity.getTitle());
    newValue.put(Activities.COLUMN_STARTDATE, activity.getStartDate());
    newValue.put(Activities.COLUMN_ENDDATE, activity.getEndDate());
    newValue.put(Activities.COLUMN_NOTES, activity.getNotes());
    newValue.put(Activities.COLUMN_ORGS, activity.getOrgs());
    newValue.put(Activities.COLUMN_COMMS, activity.getComms());
    newValue.put(Activities.COLUMN_INITIATIVES, activity.getInitiatives());
    newValue.put(Activities.COLUMN_PROJECTID, activity.getProjectid());
    String whereClause = Activities.COLUMN_ID + '=' + activity.getId();
    // Update the item into the database
    writeDatabase.update(Activities.ACTIVITIES_TABLE, newValue, whereClause, null);
  }

  public int deleteActivities(int id) {
    String whereClause = Activities.COLUMN_ID + '=' + id;
    // Return the total number of rows removed
    return writeDatabase.delete(Activities.ACTIVITIES_TABLE, whereClause, null);
  }
}
