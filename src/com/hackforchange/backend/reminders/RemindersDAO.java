package com.hackforchange.backend.reminders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.hackforchange.backend.GlobalDatabaseHelper;
import com.hackforchange.models.reminders.Reminders;

import java.util.ArrayList;

/*
 * DAO object to update/delete/add reminders
 */
public class RemindersDAO {
  private GlobalDatabaseHelper opener;
  private SQLiteDatabase readDatabase;
  private SQLiteDatabase writeDatabase;

  public RemindersDAO(Context context) {
    this.opener = GlobalDatabaseHelper.getInstance(context);
    this.readDatabase = opener.getReadableDatabase();
    this.writeDatabase = opener.getWritableDatabase();
    this.writeDatabase.execSQL("PRAGMA foreign_keys=ON"); // make sure to turn foreign keys constraints on
  }

  public ArrayList<Reminders> getAllReminders() {
    ArrayList<Reminders> output = null;
    String[] columnsToRead = new String[3];
    columnsToRead[0] = Reminders.COLUMN_ID;
    columnsToRead[1] = Reminders.COLUMN_ACTIVITYID;
    columnsToRead[2] = Reminders.COLUMN_REMINDTIME;

    Cursor returnData = readDatabase.query(Reminders.REMINDERS_TABLE, columnsToRead, null, null, null, null, null);
    output = extractReminders(returnData);
    return output;
  }

  public ArrayList<Reminders> getAllRemindersForActivityId(int activityid) {
    ArrayList<Reminders> output = null;
    String[] columnsToRead = new String[3];
    columnsToRead[0] = Reminders.COLUMN_ID;
    columnsToRead[1] = Reminders.COLUMN_ACTIVITYID;
    columnsToRead[2] = Reminders.COLUMN_REMINDTIME;

    String whereClause = Reminders.COLUMN_ACTIVITYID + '=' + activityid;
    Cursor returnData = readDatabase.query(Reminders.REMINDERS_TABLE, columnsToRead,
      whereClause, null, null, null, null);
    output = extractReminders(returnData);
    return output;
  }

  private ArrayList<Reminders> extractReminders(Cursor returnData) {
    // The output ArrayList is initialized
    ArrayList<Reminders> output = new ArrayList<Reminders>();
    // Move the counter to the first item in the return data
    returnData.moveToFirst();
    int count = 0;
    // While there are still values in the return data
    while (!returnData.isAfterLast()) {
      // Add the new Reminders to the ArrayList
      Reminders r = new Reminders();
      r.setId(Integer.parseInt(returnData.getString(0)));
      r.setActivityid(Integer.parseInt(returnData.getString(1)));
      r.setRemindTime(returnData.getLong(2));
      output.add(count, r);
      // Advance the Cursor
      returnData.moveToNext();
      // Advance the counter
      count++;
    }
    // Return the ArrayList
    return output;
  }

  public void addReminders(Reminders reminder) {
    ContentValues newValue = new ContentValues(2);
    newValue.put(Reminders.COLUMN_ACTIVITYID, reminder.getActivityid());
    newValue.put(Reminders.COLUMN_REMINDTIME, reminder.getRemindTime());

    // Insert the item into the database
    writeDatabase.insert(Reminders.REMINDERS_TABLE, null, newValue);
  }

  public void updateReminders(Reminders reminder) {
    ContentValues newValue = new ContentValues(2);
    newValue.put(Reminders.COLUMN_ACTIVITYID, reminder.getActivityid());
    newValue.put(Reminders.COLUMN_REMINDTIME, reminder.getRemindTime());
    String whereClause = Reminders.COLUMN_ID + '=' + reminder.getId();
    // Update the item into the database
    writeDatabase.update(Reminders.REMINDERS_TABLE, newValue, whereClause, null);
  }

  public int deleteReminders(int id) {
    String whereClause = Reminders.COLUMN_ID + '=' + id;
    // Return the total number of rows removed
    return writeDatabase.delete(Reminders.REMINDERS_TABLE, whereClause, null);
  }
}
