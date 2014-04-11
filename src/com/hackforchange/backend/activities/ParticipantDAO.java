package com.hackforchange.backend.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;

import com.hackforchange.backend.GlobalDatabaseHelper;
import com.hackforchange.models.activities.Participant;

/*
 * DAO object to update/delete/add participation
 */
public class ParticipantDAO {
  private GlobalDatabaseHelper opener;
  private SQLiteDatabase readDatabase;
  private SQLiteDatabase writeDatabase;
  private Context context;

  public ParticipantDAO(Context context) {
    this.opener = GlobalDatabaseHelper.getInstance(context);
    this.readDatabase = opener.getReadableDatabase();
    this.writeDatabase = opener.getWritableDatabase();
    this.context = context;
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
   * Participant object being written using {@link com.hackforchange.models.activities.Participant#setParticipationId(int)}
   * @param participantList
   */
  public void addParticipants(List<Participant> participantList){
    openDB();
    for(Participant p: participantList){
      writeNewParticipantData(p);
    }
    closeDB();
  }

  /**
   * Add a single participant.
   * <p><b>WARNING</b>: Before you call this method, you must set the participation id in every
   * Participant object being written using {@link com.hackforchange.models.activities.Participant#setParticipationId(int)}
   * @param participantList
   */
  public void addParticipant(Participant participant) {
    openDB();
    writeNewParticipantData(participant);
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
  private void writeNewParticipantData(Participant participant) {
    ContentValues newValue = new ContentValues(7);

    participant.setSignaturePath(saveSignatureBitmap(participant));

    newValue.put(Participant.COLUMN_PARTICIPATIONID, participant.getParticipationId());
    newValue.put(Participant.COLUMN_NAME, participant.getName());
    newValue.put(Participant.COLUMN_PHONENUMBER, participant.getPhoneNumber());
    newValue.put(Participant.COLUMN_VILLAGE, participant.getVillage());
    newValue.put(Participant.COLUMN_AGE, participant.getAge());
    newValue.put(Participant.COLUMN_GENDER, participant.getGender());
    newValue.put(Participant.COLUMN_SIGNATUREPATH, participant.getSignaturePath());

    // Insert the item into the database
    writeDatabase.insert(Participant.PARTICIPANT_TABLE, null, newValue);
  }

  /**
   * Saves the signature image to external storage if available or falls back to internal storage if not. 
   * Returns the URI of the image just saved
   * @param signatureBitmap
   * @return URI of image just saved
   */
  private String saveSignatureBitmap(Participant participant) {
    Bitmap signatureBitmapToSave = participant.getSignatureBitmap();
    String timeStamp = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss_").format(new Date());
    String signatureFileName = timeStamp+participant.getParticipationId()+"_"+participant.getName()+".png";

    File signatureOutputFile = getOutputSignatureFileOnExternalOrInternalStorage(signatureFileName);

    try {
      FileOutputStream fos = new FileOutputStream(signatureOutputFile);
      signatureBitmapToSave.compress(Bitmap.CompressFormat.PNG, 90, fos);
      fos.close();
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    }

    return signatureOutputFile.getAbsolutePath();
  }

  /**
   * Returns a file that we can write the signature to. Will either be on external storage if available
   * or on internal storage, if not.
   * @param signatureFileName file name to create
   * @return File object that we can write to
   */
  private File getOutputSignatureFileOnExternalOrInternalStorage(String signatureFileName) {
    File signaturesDir = new File(context.getFilesDir(), "signatures"); //fallback onto internal storage

    if(isExternalStorageWritable())
      signaturesDir = new File(context.getExternalFilesDir(null), "signatures");

    if(!signaturesDir.isDirectory())
      signaturesDir.mkdirs();

    File signatureFile = new File(signaturesDir.getPath() + File.separator + signatureFileName);  
    return signatureFile;
  }

  public boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return true;
    }
    return false;
  }

  public ArrayList<Participant> getAllParticipantsForParticipationId(int participationid) {
    openDB();
    ArrayList<Participant> output = null;
    String[] columnsToRead = new String[8];
    columnsToRead[0] = Participant.COLUMN_ID;
    columnsToRead[1] = Participant.COLUMN_NAME;
    columnsToRead[2] = Participant.COLUMN_PHONENUMBER;
    columnsToRead[3] = Participant.COLUMN_VILLAGE;
    columnsToRead[4] = Participant.COLUMN_AGE;
    columnsToRead[5] = Participant.COLUMN_GENDER;
    columnsToRead[6] = Participant.COLUMN_PARTICIPATIONID;
    columnsToRead[7] = Participant.COLUMN_SIGNATUREPATH;

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
      p.setParticipationId(returnData.getInt(6));
      p.setSignaturePath(returnData.getString(7));

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
