package com.hackforchange.backend.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.hackforchange.backend.GlobalDatabaseHelper;
import com.hackforchange.models.activities.Participation;

import java.util.ArrayList;

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
        this.writeDatabase.execSQL("PRAGMA foreign_keys=ON"); // make sure to turn foreign keys constraints on
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
        String[] columnsToRead = new String[8];
        columnsToRead[0] = Participation.COLUMN_ID;
        columnsToRead[1] = Participation.COLUMN_REMINDERID;
        columnsToRead[2] = Participation.COLUMN_MEN;
        columnsToRead[3] = Participation.COLUMN_WOMEN;
        columnsToRead[4] = Participation.COLUMN_DATE;
        columnsToRead[5] = Participation.COLUMN_ISSERVICED;
        columnsToRead[6] = Participation.COLUMN_ACTIVITYID;
        columnsToRead[7] = Participation.COLUMN_NOTES;

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
        String[] columnsToRead = new String[8];
        columnsToRead[0] = Participation.COLUMN_ID;
        columnsToRead[1] = Participation.COLUMN_REMINDERID;
        columnsToRead[2] = Participation.COLUMN_MEN;
        columnsToRead[3] = Participation.COLUMN_WOMEN;
        columnsToRead[4] = Participation.COLUMN_DATE;
        columnsToRead[5] = Participation.COLUMN_ISSERVICED;
        columnsToRead[6] = Participation.COLUMN_ACTIVITYID;
        columnsToRead[7] = Participation.COLUMN_NOTES;

        String whereClause = Participation.COLUMN_REMINDERID + '=' + reminderid + " and " + Participation.COLUMN_ISSERVICED + '=' + "'false'";
        Cursor returnData = readDatabase.query(Participation.PARTICIPATION_TABLE, columnsToRead,
                whereClause, null, null, null, null);
        output = extractParticipation(returnData);
        closeDB();
        return output;
    }

    public ArrayList<Participation> getAllParticipationsForReminderId(int reminderid) {
        openDB();
        ArrayList<Participation> output = null;
        String[] columnsToRead = new String[8];
        columnsToRead[0] = Participation.COLUMN_ID;
        columnsToRead[1] = Participation.COLUMN_REMINDERID;
        columnsToRead[2] = Participation.COLUMN_MEN;
        columnsToRead[3] = Participation.COLUMN_WOMEN;
        columnsToRead[4] = Participation.COLUMN_DATE;
        columnsToRead[5] = Participation.COLUMN_ISSERVICED;
        columnsToRead[6] = Participation.COLUMN_ACTIVITYID;
        columnsToRead[7] = Participation.COLUMN_NOTES;

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
        String[] columnsToRead = new String[8];
        columnsToRead[0] = Participation.COLUMN_ID;
        columnsToRead[1] = Participation.COLUMN_REMINDERID;
        columnsToRead[2] = Participation.COLUMN_MEN;
        columnsToRead[3] = Participation.COLUMN_WOMEN;
        columnsToRead[4] = Participation.COLUMN_DATE;
        columnsToRead[5] = Participation.COLUMN_ISSERVICED;
        columnsToRead[6] = Participation.COLUMN_ACTIVITYID;
        columnsToRead[7] = Participation.COLUMN_NOTES;

        String whereClause = Participation.COLUMN_ACTIVITYID + '=' + activityid;
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
            Participation p = new Participation();
            p.setId(returnData.getInt(0));
            p.setReminderid(returnData.getInt(1));
            p.setMen(returnData.getInt(2));
            p.setWomen(returnData.getInt(3));
            p.setDate(returnData.getLong(4));
            p.setServiced(Boolean.parseBoolean(returnData.getString(5)));
            p.setActivityid(returnData.getInt(6));
            p.setNotes(returnData.getString(7));
            output.add(count, p);
            // Advance the Cursor
            returnData.moveToNext();
            // Advance the counter
            count++;
        }
        // Return the ArrayList
        return output;
    }

    public Participation getParticipationWithId(int id) {
        openDB();
        String[] columnsToRead = new String[8];
        columnsToRead[0] = Participation.COLUMN_ID;
        columnsToRead[1] = Participation.COLUMN_REMINDERID;
        columnsToRead[2] = Participation.COLUMN_MEN;
        columnsToRead[3] = Participation.COLUMN_WOMEN;
        columnsToRead[4] = Participation.COLUMN_DATE;
        columnsToRead[5] = Participation.COLUMN_ISSERVICED;
        columnsToRead[6] = Participation.COLUMN_ACTIVITYID;
        columnsToRead[7] = Participation.COLUMN_NOTES;
        String whereClause = Participation.COLUMN_ID + '=' + id;
        Cursor returnData = readDatabase.query(Participation.PARTICIPATION_TABLE, columnsToRead,
                whereClause, null, null, null, null);
        returnData.moveToFirst();
        Participation p = new Participation();
        Log.e("burra", "pdao get participation with id " + id);
        p.setId(id);
        p.setReminderid(returnData.getInt(1));
        p.setMen(returnData.getInt(2));
        p.setWomen(returnData.getInt(3));
        p.setDate(returnData.getLong(4));
        p.setServiced(Boolean.parseBoolean(returnData.getString(5)));
        p.setActivityid(returnData.getInt(6));
        p.setNotes(returnData.getString(7));
        closeDB();
        // Return the constructed Participation object
        return p;
    }

    public int addParticipation(Participation participation) {
        openDB();
        ContentValues newValue = new ContentValues(7);
        newValue.put(Participation.COLUMN_REMINDERID, participation.getReminderid());
        newValue.put(Participation.COLUMN_ACTIVITYID, participation.getActivityid());
        newValue.put(Participation.COLUMN_MEN, participation.getMen());
        newValue.put(Participation.COLUMN_WOMEN, participation.getWomen());
        newValue.put(Participation.COLUMN_NOTES, participation.getNotes());
        //DateFormat parser = new SimpleDateFormat("MM/dd/yyyy, EEEE, hh:mm aaa"); // example: 07/04/2013, Thursday, 6:13 PM
        //newValue.put(Participation.COLUMN_DATE, parser.format(participation.getDate()));
        newValue.put(Participation.COLUMN_DATE, participation.getDate());
        newValue.put(Participation.COLUMN_ISSERVICED, participation.isServiced());
        // Insert the item into the database
        writeDatabase.insert(Participation.PARTICIPATION_TABLE, null, newValue);

        // return the id of the activity just created. This will be used as the foreign key for the reminders table
        Cursor returnData = readDatabase.rawQuery("select seq from sqlite_sequence where name=?", new String[]{Participation.PARTICIPATION_TABLE});
        int retVal = -1;
        if (returnData != null && returnData.moveToFirst()) {
            retVal = returnData.getInt(0);
        }
      closeDB();
      return retVal;
    }

    public int getLargestParticipationId(){
      openDB();
      // return the largest participation id so far. Used to add a quick participation record that is not really
      // tied to a reminder.
      Cursor returnData = readDatabase.rawQuery("select seq from sqlite_sequence where name=?", new String[]{Participation.PARTICIPATION_TABLE});
      int retVal = -1;
      if (returnData != null && returnData.moveToFirst()) {
        retVal = returnData.getInt(0);
      }
      closeDB();
      return retVal;
    }

    public void updateParticipation(Participation participation) {
        openDB();
        ContentValues newValue = new ContentValues(7);
        newValue.put(Participation.COLUMN_REMINDERID, participation.getReminderid());
        newValue.put(Participation.COLUMN_MEN, participation.getMen());
        newValue.put(Participation.COLUMN_WOMEN, participation.getWomen());
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
        int numItemsDeleted = writeDatabase.delete(Participation.PARTICIPATION_TABLE, whereClause, null);
        closeDB();
        return numItemsDeleted;
    }
}
