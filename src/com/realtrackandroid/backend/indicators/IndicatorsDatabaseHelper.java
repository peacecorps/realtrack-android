package com.realtrackandroid.backend.indicators;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class IndicatorsDatabaseHelper extends SQLiteOpenHelper {
    private String dbPath;
    private static String DB_NAME = "indicators.sqlite";
    private SQLiteDatabase database; 
    private final Context context;

    public IndicatorsDatabaseHelper(Context context) {
      super(context, DB_NAME, null, 1);
      dbPath = context.getDatabasePath(DB_NAME).getAbsolutePath();
      this.context = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
      boolean dbExist = checkDataBase();

      if(dbExist){
        //do nothing - database already exists
      }else{
        this.getReadableDatabase();

        try {
          copyDataBaseToSystem();
        } catch (IOException e) {
          throw new Error("Error copying database");
        }
      }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
      SQLiteDatabase checkDB = null;

      try{
        checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);

      }catch(SQLiteException e){
        //database does't exist yet.
      }

      if(checkDB != null){
        checkDB.close();
      }

      return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBaseToSystem() throws IOException{
      //Open your local db as an input stream
      InputStream myInput = context.getAssets().open(DB_NAME);

      // Path to the just created empty db
      String outFileName = dbPath;

      //Open the empty db as the output stream
      OutputStream myOutput = new FileOutputStream(outFileName);

      //transfer bytes from the inputfile to the outputfile
      byte[] buffer = new byte[1024];
      int length;
      while ((length = myInput.read(buffer))>0){
        myOutput.write(buffer, 0, length);
      }

      //Close the streams
      myOutput.flush();
      myOutput.close();
      myInput.close();

    }

    public void openDataBase() throws SQLException{
      //Open the database
      database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
      if(database != null)
        database.close();
      super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

  }
