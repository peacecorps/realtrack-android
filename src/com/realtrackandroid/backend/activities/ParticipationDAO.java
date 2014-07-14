package com.realtrackandroid.backend.activities;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.realtrackandroid.backend.GlobalDatabaseHelper;
import com.realtrackandroid.models.activities.Participation;

/*
 * DAO object to update/delete/add participation
 */
public class ParticipationDAO {
  private GlobalDatabaseHelper opener;

  private SQLiteDatabase readDatabase;

  private SQLiteDatabase writeDatabase;

  public ParticipationDAO(Context context) {
    this.opener = GlobalDatabaseHelper.getInstance(context);
    this.readDatabase = opener.getReadableDatabase();
    this.writeDatabase = opener.getWritableDatabase();
    closeDB();
  }

  private void openDB() {
    if (!readDatabase.isOpen()) {
      readDatabase = opener.getReadableDatabase();
    }
    if (!writeDatabase.isOpen()) {
      writeDatabase = opener.getWritableDatabase();
    }
  }

  private void closeDB() {
    if (readDatabase.isOpen()) {
      readDatabase.close();
    }
    if (writeDatabase.isOpen()) {
      writeDatabase.close();
    }
  }

  public ArrayList<Participation> getAllUnservicedParticipations() {
    openDB();
    ArrayList<Participation> output = null;
    String[] columnsToRead = new String[22];
    columnsToRead[0] = Participation.COLUMN_ID;
    columnsToRead[1] = Participation.COLUMN_REMINDERID;
    columnsToRead[2] = Participation.COLUMN_MEN09;
    columnsToRead[3] = Participation.COLUMN_MEN1824;
    columnsToRead[4] = Participation.COLUMN_MENOVER25;
    columnsToRead[5] = Participation.COLUMN_WOMEN09;
    columnsToRead[6] = Participation.COLUMN_WOMEN1824;
    columnsToRead[7] = Participation.COLUMN_WOMENOVER25;
    columnsToRead[8] = Participation.COLUMN_DATE;
    columnsToRead[9] = Participation.COLUMN_ISSERVICED;
    columnsToRead[10] = Participation.COLUMN_ACTIVITYID;
    columnsToRead[11] = Participation.COLUMN_NOTES;
    columnsToRead[12] = Participation.COLUMN_MEN1017;
    columnsToRead[13] = Participation.COLUMN_WOMEN1017;
    columnsToRead[14] = Participation.COLUMN_SPMEN09;
    columnsToRead[15] = Participation.COLUMN_SPMEN1017;
    columnsToRead[16] = Participation.COLUMN_SPMEN1824;
    columnsToRead[17] = Participation.COLUMN_SPMENOVER25;
    columnsToRead[18] = Participation.COLUMN_SPWOMEN09;
    columnsToRead[19] = Participation.COLUMN_SPWOMEN1017;
    columnsToRead[20] = Participation.COLUMN_SPWOMEN1824;
    columnsToRead[21] = Participation.COLUMN_SPWOMENOVER25;

    String whereClause = Participation.COLUMN_ISSERVICED + '=' + "'false'";
    Cursor returnData = readDatabase.query(Participation.PARTICIPATION_TABLE, columnsToRead,
            whereClause, null, null, null, null);
    output = extractParticipation(returnData);
    closeDB();
    return output;
  }

  public ArrayList<Participation> getAllUnservicedParticipationsForReminderId(int reminderid) {
    openDB();
    ArrayList<Participation> output = null;
    String[] columnsToRead = new String[22];
    columnsToRead[0] = Participation.COLUMN_ID;
    columnsToRead[1] = Participation.COLUMN_REMINDERID;
    columnsToRead[2] = Participation.COLUMN_MEN09;
    columnsToRead[3] = Participation.COLUMN_MEN1824;
    columnsToRead[4] = Participation.COLUMN_MENOVER25;
    columnsToRead[5] = Participation.COLUMN_WOMEN09;
    columnsToRead[6] = Participation.COLUMN_WOMEN1824;
    columnsToRead[7] = Participation.COLUMN_WOMENOVER25;
    columnsToRead[8] = Participation.COLUMN_DATE;
    columnsToRead[9] = Participation.COLUMN_ISSERVICED;
    columnsToRead[10] = Participation.COLUMN_ACTIVITYID;
    columnsToRead[11] = Participation.COLUMN_NOTES;
    columnsToRead[12] = Participation.COLUMN_MEN1017;
    columnsToRead[13] = Participation.COLUMN_WOMEN1017;
    columnsToRead[14] = Participation.COLUMN_SPMEN09;
    columnsToRead[15] = Participation.COLUMN_SPMEN1017;
    columnsToRead[16] = Participation.COLUMN_SPMEN1824;
    columnsToRead[17] = Participation.COLUMN_SPMENOVER25;
    columnsToRead[18] = Participation.COLUMN_SPWOMEN09;
    columnsToRead[19] = Participation.COLUMN_SPWOMEN1017;
    columnsToRead[20] = Participation.COLUMN_SPWOMEN1824;
    columnsToRead[21] = Participation.COLUMN_SPWOMENOVER25;

    String whereClause = Participation.COLUMN_REMINDERID + '=' + reminderid + " and "
            + Participation.COLUMN_ISSERVICED + '=' + "'false'";
    Cursor returnData = readDatabase.query(Participation.PARTICIPATION_TABLE, columnsToRead,
            whereClause, null, null, null, null);
    output = extractParticipation(returnData);
    closeDB();
    return output;
  }

  public ArrayList<Participation> getAllParticipationsForReminderId(int reminderid) {
    openDB();
    ArrayList<Participation> output = null;
    String[] columnsToRead = new String[22];
    columnsToRead[0] = Participation.COLUMN_ID;
    columnsToRead[1] = Participation.COLUMN_REMINDERID;
    columnsToRead[2] = Participation.COLUMN_MEN09;
    columnsToRead[3] = Participation.COLUMN_MEN1824;
    columnsToRead[4] = Participation.COLUMN_MENOVER25;
    columnsToRead[5] = Participation.COLUMN_WOMEN09;
    columnsToRead[6] = Participation.COLUMN_WOMEN1824;
    columnsToRead[7] = Participation.COLUMN_WOMENOVER25;
    columnsToRead[8] = Participation.COLUMN_DATE;
    columnsToRead[9] = Participation.COLUMN_ISSERVICED;
    columnsToRead[10] = Participation.COLUMN_ACTIVITYID;
    columnsToRead[11] = Participation.COLUMN_NOTES;
    columnsToRead[12] = Participation.COLUMN_MEN1017;
    columnsToRead[13] = Participation.COLUMN_WOMEN1017;
    columnsToRead[14] = Participation.COLUMN_SPMEN09;
    columnsToRead[15] = Participation.COLUMN_SPMEN1017;
    columnsToRead[16] = Participation.COLUMN_SPMEN1824;
    columnsToRead[17] = Participation.COLUMN_SPMENOVER25;
    columnsToRead[18] = Participation.COLUMN_SPWOMEN09;
    columnsToRead[19] = Participation.COLUMN_SPWOMEN1017;
    columnsToRead[20] = Participation.COLUMN_SPWOMEN1824;
    columnsToRead[21] = Participation.COLUMN_SPWOMENOVER25;

    String whereClause = Participation.COLUMN_REMINDERID + '=' + reminderid;
    Cursor returnData = readDatabase.query(Participation.PARTICIPATION_TABLE, columnsToRead,
            whereClause, null, null, null, null);
    output = extractParticipation(returnData);
    closeDB();
    return output;
  }

  public ArrayList<Participation> getAllParticipationsForActivityId(int activityid) {
    openDB();
    ArrayList<Participation> output = null;
    String[] columnsToRead = new String[22];
    columnsToRead[0] = Participation.COLUMN_ID;
    columnsToRead[1] = Participation.COLUMN_REMINDERID;
    columnsToRead[2] = Participation.COLUMN_MEN09;
    columnsToRead[3] = Participation.COLUMN_MEN1824;
    columnsToRead[4] = Participation.COLUMN_MENOVER25;
    columnsToRead[5] = Participation.COLUMN_WOMEN09;
    columnsToRead[6] = Participation.COLUMN_WOMEN1824;
    columnsToRead[7] = Participation.COLUMN_WOMENOVER25;
    columnsToRead[8] = Participation.COLUMN_DATE;
    columnsToRead[9] = Participation.COLUMN_ISSERVICED;
    columnsToRead[10] = Participation.COLUMN_ACTIVITYID;
    columnsToRead[11] = Participation.COLUMN_NOTES;
    columnsToRead[12] = Participation.COLUMN_MEN1017;
    columnsToRead[13] = Participation.COLUMN_WOMEN1017;
    columnsToRead[14] = Participation.COLUMN_SPMEN09;
    columnsToRead[15] = Participation.COLUMN_SPMEN1017;
    columnsToRead[16] = Participation.COLUMN_SPMEN1824;
    columnsToRead[17] = Participation.COLUMN_SPMENOVER25;
    columnsToRead[18] = Participation.COLUMN_SPWOMEN09;
    columnsToRead[19] = Participation.COLUMN_SPWOMEN1017;
    columnsToRead[20] = Participation.COLUMN_SPWOMEN1824;
    columnsToRead[21] = Participation.COLUMN_SPWOMENOVER25;

    String whereClause = Participation.COLUMN_ACTIVITYID + '=' + activityid;
    Cursor returnData = readDatabase.query(Participation.PARTICIPATION_TABLE, columnsToRead,
            whereClause, null, null, null, null);
    output = extractParticipation(returnData);
    closeDB();
    return output;
  }

  public ArrayList<Participation> getServicedParticipationsForActivityId(int activityid) {
    openDB();
    ArrayList<Participation> output = null;
    String[] columnsToRead = new String[22];
    columnsToRead[0] = Participation.COLUMN_ID;
    columnsToRead[1] = Participation.COLUMN_REMINDERID;
    columnsToRead[2] = Participation.COLUMN_MEN09;
    columnsToRead[3] = Participation.COLUMN_MEN1824;
    columnsToRead[4] = Participation.COLUMN_MENOVER25;
    columnsToRead[5] = Participation.COLUMN_WOMEN09;
    columnsToRead[6] = Participation.COLUMN_WOMEN1824;
    columnsToRead[7] = Participation.COLUMN_WOMENOVER25;
    columnsToRead[8] = Participation.COLUMN_DATE;
    columnsToRead[9] = Participation.COLUMN_ISSERVICED;
    columnsToRead[10] = Participation.COLUMN_ACTIVITYID;
    columnsToRead[11] = Participation.COLUMN_NOTES;
    columnsToRead[12] = Participation.COLUMN_MEN1017;
    columnsToRead[13] = Participation.COLUMN_WOMEN1017;
    columnsToRead[14] = Participation.COLUMN_SPMEN09;
    columnsToRead[15] = Participation.COLUMN_SPMEN1017;
    columnsToRead[16] = Participation.COLUMN_SPMEN1824;
    columnsToRead[17] = Participation.COLUMN_SPMENOVER25;
    columnsToRead[18] = Participation.COLUMN_SPWOMEN09;
    columnsToRead[19] = Participation.COLUMN_SPWOMEN1017;
    columnsToRead[20] = Participation.COLUMN_SPWOMEN1824;
    columnsToRead[21] = Participation.COLUMN_SPWOMENOVER25;

    String whereClause = Participation.COLUMN_ACTIVITYID + '=' + activityid + " and "
            + Participation.COLUMN_ISSERVICED + '=' + "'true'";
    Cursor returnData = readDatabase.query(Participation.PARTICIPATION_TABLE, columnsToRead,
            whereClause, null, null, null, null);
    output = extractParticipation(returnData);
    closeDB();
    return output;
  }

  private ArrayList<Participation> extractParticipation(Cursor returnData) {
    // The output ArrayList is initialized
    ArrayList<Participation> output = new ArrayList<Participation>();
    // Move the counter to the first item in the return data
    returnData.moveToFirst();
    int count = 0;
    // While there are still values in the return data
    while (!returnData.isAfterLast()) {
      // Add the new Participation to the ArrayList
      Participation p = createNewParticipation(returnData);
      output.add(count, p);
      // Advance the Cursor
      returnData.moveToNext();
      // Advance the counter
      count++;
    }
    // Return the ArrayList
    return output;
  }

  private Participation createNewParticipation(Cursor returnData) {
    Participation p = new Participation();
    p.setId(returnData.getInt(0));
    p.setReminderid(returnData.getInt(1));
    p.setMen09(returnData.getInt(2));
    p.setMen1824(returnData.getInt(3));
    p.setMenOver25(returnData.getInt(4));
    p.setWomen09(returnData.getInt(5));
    p.setWomen1824(returnData.getInt(6));
    p.setWomenOver25(returnData.getInt(7));
    p.setDate(returnData.getLong(8));
    p.setServiced(Boolean.parseBoolean(returnData.getString(9)));
    p.setActivityid(returnData.getInt(10));
    p.setNotes(returnData.getString(11));
    p.setMen1017(returnData.getInt(12));
    p.setWomen1017(returnData.getInt(13));
    p.setSpMen09(returnData.getInt(14));
    p.setSpMen1017(returnData.getInt(15));
    p.setSpMen1824(returnData.getInt(16));
    p.setSpMenOver25(returnData.getInt(17));
    p.setSpWomen09(returnData.getInt(18));
    p.setSpWomen1017(returnData.getInt(19));
    p.setSpWomen1824(returnData.getInt(20));
    p.setSpWomenOver25(returnData.getInt(21));
    return p;
  }

  public Participation getParticipationWithId(int id) {
    openDB();
    String[] columnsToRead = new String[22];
    columnsToRead[0] = Participation.COLUMN_ID;
    columnsToRead[1] = Participation.COLUMN_REMINDERID;
    columnsToRead[2] = Participation.COLUMN_MEN09;
    columnsToRead[3] = Participation.COLUMN_MEN1824;
    columnsToRead[4] = Participation.COLUMN_MENOVER25;
    columnsToRead[5] = Participation.COLUMN_WOMEN09;
    columnsToRead[6] = Participation.COLUMN_WOMEN1824;
    columnsToRead[7] = Participation.COLUMN_WOMENOVER25;
    columnsToRead[8] = Participation.COLUMN_DATE;
    columnsToRead[9] = Participation.COLUMN_ISSERVICED;
    columnsToRead[10] = Participation.COLUMN_ACTIVITYID;
    columnsToRead[11] = Participation.COLUMN_NOTES;
    columnsToRead[12] = Participation.COLUMN_MEN1017;
    columnsToRead[13] = Participation.COLUMN_WOMEN1017;
    columnsToRead[14] = Participation.COLUMN_SPMEN09;
    columnsToRead[15] = Participation.COLUMN_SPMEN1017;
    columnsToRead[16] = Participation.COLUMN_SPMEN1824;
    columnsToRead[17] = Participation.COLUMN_SPMENOVER25;
    columnsToRead[18] = Participation.COLUMN_SPWOMEN09;
    columnsToRead[19] = Participation.COLUMN_SPWOMEN1017;
    columnsToRead[20] = Participation.COLUMN_SPWOMEN1824;
    columnsToRead[21] = Participation.COLUMN_SPWOMENOVER25;
    String whereClause = Participation.COLUMN_ID + '=' + id;
    Cursor returnData = readDatabase.query(Participation.PARTICIPATION_TABLE, columnsToRead,
            whereClause, null, null, null, null);
    returnData.moveToFirst();
    Participation p = new Participation();
    p.setId(id);
    p.setReminderid(returnData.getInt(1));
    p.setMen09(returnData.getInt(2));
    p.setMen1824(returnData.getInt(3));
    p.setMenOver25(returnData.getInt(4));
    p.setWomen09(returnData.getInt(5));
    p.setWomen1824(returnData.getInt(6));
    p.setWomenOver25(returnData.getInt(7));
    p.setDate(returnData.getLong(8));
    p.setServiced(Boolean.parseBoolean(returnData.getString(9)));
    p.setActivityid(returnData.getInt(10));
    p.setNotes(returnData.getString(11));
    p.setMen1017(returnData.getInt(12));
    p.setWomen1017(returnData.getInt(13));
    p.setSpMen09(returnData.getInt(14));
    p.setSpMen1017(returnData.getInt(15));
    p.setSpMen1824(returnData.getInt(16));
    p.setSpMenOver25(returnData.getInt(17));
    p.setSpWomen09(returnData.getInt(18));
    p.setSpWomen1017(returnData.getInt(19));
    p.setSpWomen1824(returnData.getInt(20));
    p.setSpWomenOver25(returnData.getInt(21));
    closeDB();
    // Return the constructed Participation object
    return p;
  }

  public int addParticipation(Participation participation) {
    openDB();
    ContentValues newValue = new ContentValues(21);
    newValue.put(Participation.COLUMN_REMINDERID, participation.getReminderid());
    newValue.put(Participation.COLUMN_ACTIVITYID, participation.getActivityid());
    newValue.put(Participation.COLUMN_MEN09, participation.getMen09());
    newValue.put(Participation.COLUMN_MEN1017, participation.getMen1017());
    newValue.put(Participation.COLUMN_MEN1824, participation.getMen1824());
    newValue.put(Participation.COLUMN_MENOVER25, participation.getMenOver25());
    newValue.put(Participation.COLUMN_WOMEN09, participation.getWomen09());
    newValue.put(Participation.COLUMN_WOMEN1017, participation.getWomen1017());
    newValue.put(Participation.COLUMN_WOMEN1824, participation.getWomen1824());
    newValue.put(Participation.COLUMN_WOMENOVER25, participation.getWomenOver25());
    newValue.put(Participation.COLUMN_SPMEN09, participation.getSpMen09());
    newValue.put(Participation.COLUMN_SPMEN1017, participation.getSpMen1017());
    newValue.put(Participation.COLUMN_SPMEN1824, participation.getSpMen1824());
    newValue.put(Participation.COLUMN_SPMENOVER25, participation.getSpMenOver25());
    newValue.put(Participation.COLUMN_SPWOMEN09, participation.getSpWomen09());
    newValue.put(Participation.COLUMN_SPWOMEN1017, participation.getSpWomen1017());
    newValue.put(Participation.COLUMN_SPWOMEN1824, participation.getSpWomen1824());
    newValue.put(Participation.COLUMN_SPWOMENOVER25, participation.getSpWomenOver25());
    newValue.put(Participation.COLUMN_NOTES, participation.getNotes());
    // DateFormat parser = new SimpleDateFormat("MM/dd/yyyy, EEEE, hh:mm aaa"); // example:
    // 07/04/2013, Thursday, 6:13 PM
    // newValue.put(Participation.COLUMN_DATE, parser.format(participation.getDate()));
    newValue.put(Participation.COLUMN_DATE, participation.getDate());
    newValue.put(Participation.COLUMN_ISSERVICED, participation.isServiced() ? "true" : "false");
    // Insert the item into the database
    writeDatabase.insert(Participation.PARTICIPATION_TABLE, null, newValue);

    // return the id of the activity just created. This will be used as the foreign key for the
    // reminders table
    Cursor returnData = readDatabase.rawQuery("select seq from sqlite_sequence where name=?",
            new String[] { Participation.PARTICIPATION_TABLE });
    int retVal = -1;
    if (returnData != null && returnData.moveToFirst()) {
      retVal = returnData.getInt(0);
    }
    closeDB();
    return retVal;
  }

  public int getLargestParticipationId() {
    openDB();
    // return the largest participation id so far. Used to add a quick participation record that is
    // not really
    // tied to a reminder.
    Cursor returnData = readDatabase.rawQuery("select seq from sqlite_sequence where name=?",
            new String[] { Participation.PARTICIPATION_TABLE });
    int retVal = -1;
    if (returnData != null && returnData.moveToFirst()) {
      retVal = returnData.getInt(0);
    }
    closeDB();
    return retVal;
  }

  public void updateParticipation(Participation participation) {
    openDB();
    ContentValues newValue = new ContentValues(21);
    newValue.put(Participation.COLUMN_REMINDERID, participation.getReminderid());
    newValue.put(Participation.COLUMN_MEN09, participation.getMen09());
    newValue.put(Participation.COLUMN_MEN1017, participation.getMen1017());
    newValue.put(Participation.COLUMN_MEN1824, participation.getMen1824());
    newValue.put(Participation.COLUMN_MENOVER25, participation.getMenOver25());
    newValue.put(Participation.COLUMN_WOMEN09, participation.getWomen09());
    newValue.put(Participation.COLUMN_WOMEN1017, participation.getWomen1017());
    newValue.put(Participation.COLUMN_WOMEN1824, participation.getWomen1824());
    newValue.put(Participation.COLUMN_WOMENOVER25, participation.getWomenOver25());
    newValue.put(Participation.COLUMN_SPMEN09, participation.getSpMen09());
    newValue.put(Participation.COLUMN_SPMEN1017, participation.getSpMen1017());
    newValue.put(Participation.COLUMN_SPMEN1824, participation.getSpMen1824());
    newValue.put(Participation.COLUMN_SPMENOVER25, participation.getSpMenOver25());
    newValue.put(Participation.COLUMN_SPWOMEN09, participation.getSpWomen09());
    newValue.put(Participation.COLUMN_SPWOMEN1017, participation.getSpWomen1017());
    newValue.put(Participation.COLUMN_SPWOMEN1824, participation.getSpWomen1824());
    newValue.put(Participation.COLUMN_SPWOMENOVER25, participation.getSpWomenOver25());
    newValue.put(Participation.COLUMN_DATE, participation.getDate());
    newValue.put(Participation.COLUMN_ISSERVICED, participation.isServiced() ? "true" : "false");
    newValue.put(Participation.COLUMN_ACTIVITYID, participation.getActivityid());
    newValue.put(Participation.COLUMN_NOTES, participation.getNotes());
    String whereClause = Participation.COLUMN_ID + '=' + participation.getId();
    // Update the item into the database
    writeDatabase.update(Participation.PARTICIPATION_TABLE, newValue, whereClause, null);
    closeDB();
  }

  public int deleteParticipation(int id) {
    openDB();
    String whereClause = Participation.COLUMN_ID + '=' + id;
    // Return the total number of rows removed
    int numItemsDeleted = writeDatabase
            .delete(Participation.PARTICIPATION_TABLE, whereClause, null);
    closeDB();
    return numItemsDeleted;
  }
}
