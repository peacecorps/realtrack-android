package com.hackforchange.backend.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.hackforchange.R;
import com.hackforchange.backend.GlobalDatabaseHelper;
import com.hackforchange.models.activities.Participant;

/*
 * DAO object to update/delete/add participation
 */
public class ParticipantDAO {
  private GlobalDatabaseHelper opener;
  private SQLiteDatabase readDatabase;
  private SQLiteDatabase writeDatabase;

  public ParticipantDAO(Context context) {
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

  /**
   * Add multiple participants at once.
   * <p><b>WARNING</b>: Before you call this method, you must set the participation id in every
   * Participant object being written using {@link com.hackforchange.models.activities.Participant#setParticipationid(int)}
   * @param participantList
   */
  public void addParticipants(List<Participant> participantList){
    openDB();
    for(Participant p: participantList){
      writeParticipantData(p);
    }
    closeDB();
  }

  /**
   * Add a single participant.
   * <p><b>WARNING</b>: Before you call this method, you must set the participation id in every
   * Participant object being written using {@link com.hackforchange.models.activities.Participant#setParticipationid(int)}
   * @param participantList
   */
  public void addParticipant(Participant participant) {
    openDB();
    writeParticipantData(participant);
    closeDB();
  }

  /**
   * We create a new method since this code is shared between addParticipants
   * and addParticipant. We could have left this code in addParticipant and
   * simply called that method from addParticipants for each participant. In that
   * case,the db would be opened and closed for EACH participant. This can negatively
   * impact performance. On the other hand, removing openDB and closeDB from 
   * from addParticipant is not safe because someone may decide to call it on its
   * own. Hence, we extract this logic into a new method that can be called from
   * addParticipants (with an openDB and a closeDB for all the participants at once)
   * as well as addParticipant (with an openDB and a closeDB for just that participant).
   * 
   * @param participant object to write to the database.
   */
  private void writeParticipantData(Participant participant) {
    ContentValues newValue = new ContentValues(6);

    newValue.put(Participant.COLUMN_PARTICIPATIONID, participant.getParticipationid());
    newValue.put(Participant.COLUMN_NAME, participant.getName());
    newValue.put(Participant.COLUMN_PHONENUMBER, participant.getPhoneNumber());
    newValue.put(Participant.COLUMN_VILLAGE, participant.getVillage());
    newValue.put(Participant.COLUMN_AGE, participant.getAge());
    newValue.put(Participant.COLUMN_GENDER, participant.getGender());

    // Insert the item into the database
    writeDatabase.insert(Participant.PARTICIPANT_TABLE, null, newValue);
  }

  public ArrayList<Participant> getAllParticipantsForParticipationId(int participationid) {
    openDB();
    ArrayList<Participant> output = null;
    String[] columnsToRead = new String[7];
    columnsToRead[0] = Participant.COLUMN_ID;
    columnsToRead[1] = Participant.COLUMN_NAME;
    columnsToRead[2] = Participant.COLUMN_PHONENUMBER;
    columnsToRead[3] = Participant.COLUMN_VILLAGE;
    columnsToRead[4] = Participant.COLUMN_AGE;
    columnsToRead[5] = Participant.COLUMN_GENDER;
    columnsToRead[6] = Participant.COLUMN_PARTICIPATIONID;

    String whereClause = Participant.COLUMN_PARTICIPATIONID + '=' + participationid;
    Cursor returnData = readDatabase.query(Participant.PARTICIPANT_TABLE, columnsToRead,
            whereClause, null, null, null, null);
    output = extractParticipant(returnData);
    closeDB();
    return output;
  }

  private ArrayList<Participant> extractParticipant(Cursor returnData) {
    // The output ArrayList is initialized
    ArrayList<Participant> output = new ArrayList<Participant>();
    // Move the counter to the first item in the return data
    returnData.moveToFirst();
    int count = 0;
    // While there are still values in the return data
    while (!returnData.isAfterLast()) {
      // Add the new Participation to the ArrayList
      Participant p = new Participant();
      p.setId(returnData.getInt(0));
      p.setName(returnData.getString(1));
      p.setPhoneNumber(returnData.getString(2));
      p.setVillage(returnData.getString(3));
      p.setAge(returnData.getInt(4));
      p.setGender(returnData.getInt(5));
      p.setParticipationid(returnData.getInt(6));
      
      output.add(count, p);
      // Advance the Cursor
      returnData.moveToNext();
      // Advance the counter
      count++;
    }
    // Return the ArrayList
    return output;
  }



}
