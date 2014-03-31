package com.hackforchange.backend.activities;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.hackforchange.R;
import com.hackforchange.backend.GlobalDatabaseHelper;
import com.hackforchange.models.activities.Participant;
import com.hackforchange.models.activities.Participation;

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
      writeDatabase.insert(Participation.PARTICIPATION_TABLE, null, newValue);
    }

}
