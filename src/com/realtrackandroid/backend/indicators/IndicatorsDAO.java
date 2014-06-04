package com.realtrackandroid.backend.indicators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.realtrackandroid.models.indicators.Indicators;

/*
 * DAO object to update/delete/add Indicators
 */
public class IndicatorsDAO {
    private IndicatorsDatabaseHelper opener;
    private SQLiteDatabase readDatabase;

    public IndicatorsDAO(Context context) {
        opener = new IndicatorsDatabaseHelper(context);

        try {
          opener.createDataBase();
        } catch (IOException ioe) {
          throw new Error("Unable to create database");
        }
        
        this.readDatabase = opener.getReadableDatabase();
        closeDB();
    }

    private void openDB() {
        if (!readDatabase.isOpen()) {
            readDatabase = opener.getReadableDatabase();
        }
    }

    private void closeDB() {
        if (readDatabase.isOpen()) {
            readDatabase.close();
        }
    }
    
    public List<String> getAllPosts(){
      openDB();
      List<String> allPosts = null;
      String queryString = "select distinct " + Indicators.COLUMN_POST + " from " + Indicators.INDICATORS_TABLE +
              " order by " + Indicators.COLUMN_POST + " asc";
      Cursor returnData = readDatabase.rawQuery(queryString, null);
      allPosts = extractStrings(returnData);
      closeDB();
      return allPosts;
    }
    
    public List<String> getAllSectors(){
      openDB();
      List<String> allSectors = null;
      String queryString = "select distinct " + Indicators.COLUMN_SECTOR + " from " + Indicators.INDICATORS_TABLE +
              " order by " + Indicators.COLUMN_SECTOR + " asc";
      Cursor returnData = readDatabase.rawQuery(queryString, null);
      allSectors = extractStrings(returnData);
      closeDB();
      return allSectors;
    }
    
    public List<String> getAllProjectsForPost(String post){
      openDB();
      List<String> allProjects = null;
      String queryString = "select distinct " + Indicators.COLUMN_PROJECT + " from " + Indicators.INDICATORS_TABLE +
                           " where " + Indicators.COLUMN_POST + " = '" + post + "'" +
                           " order by " + Indicators.COLUMN_PROJECT + " asc";
      Cursor returnData = readDatabase.rawQuery(queryString, null);
      allProjects = extractStrings(returnData);
      closeDB();
      return allProjects;
    }
    
    private ArrayList<String> extractStrings(Cursor returnData) {
      // The output ArrayList is initialized
      ArrayList<String> output = new ArrayList<String>();
      // Move the counter to the first item in the return data
      returnData.moveToFirst();
      int count = 0;
      // While there are still values in the return data
      while (!returnData.isAfterLast()) {
        output.add(count, returnData.getString(0));
        // Advance the Cursor
        returnData.moveToNext();
        // Advance the counter
        count++;
      }
      // Return the ArrayList
      return output;
    }

    public List<Indicators> getAllIndicatorsForPostAndProject(String post, String project) {
        openDB();
        ArrayList<Indicators> output = null;
        String[] columnsToRead = new String[7];
        columnsToRead[0] = Indicators.COLUMN_POST;
        columnsToRead[1] = Indicators.COLUMN_SECTOR;
        columnsToRead[2] = Indicators.COLUMN_PROJECT;
        columnsToRead[3] = Indicators.COLUMN_GOAL;
        columnsToRead[4] = Indicators.COLUMN_OBJECTIVE;
        columnsToRead[5] = Indicators.COLUMN_INDICATOR;
        columnsToRead[6] = Indicators.COLUMN_TYPE;
        String whereClause = Indicators.COLUMN_POST + " = '" + post + "' and " + Indicators.COLUMN_PROJECT + " = '" + project + "'";
        String orderByClause = Indicators.COLUMN_GOAL + ", " + Indicators.COLUMN_OBJECTIVE + ", " + Indicators.COLUMN_TYPE + " desc"; //outputs BEFORE outcomes
        Cursor returnData = readDatabase.query(Indicators.INDICATORS_TABLE, columnsToRead,
            whereClause, null, null, null, orderByClause);
        output = extractIndicators(returnData);
        closeDB();
        return output;
    }

    private ArrayList<Indicators> extractIndicators(Cursor returnData) {
        // The output ArrayList is initialized
        ArrayList<Indicators> output = new ArrayList<Indicators>();
        // Move the counter to the first item in the return data
        returnData.moveToFirst();
        int count = 0;
        // While there are still values in the return data
        while (!returnData.isAfterLast()) {
            // Add the new Indicators to the ArrayList
            Indicators i = new Indicators();
            i.setPost(returnData.getString(0));
            i.setSector(returnData.getString(1));
            i.setProject(returnData.getString(2));
            i.setGoal(returnData.getString(3));
            i.setObjective(returnData.getString(4));
            i.setIndicator(returnData.getString(5));
            i.setType(returnData.getString(6));
            output.add(count, i);
            // Advance the Cursor
            returnData.moveToNext();
            // Advance the counter
            count++;
        }
        // Return the ArrayList
        return output;
    }
}
