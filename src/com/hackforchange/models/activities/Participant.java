package com.hackforchange.models.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import com.hackforchange.R;

/**
 * ********************************************************************************************************************
 * Models the representation of a participant
 * Implements Parcelable because we're going to be passing Participant objects back and forth between activities.
 * ********************************************************************************************************************
 */
public class Participant implements Parcelable {
  public static final int MALE = 0;
  public static final int FEMALE = 1;
  
  // Instance properties
  private int id; // used to modify an existing Activity. Set in ActivitiesDAO
  private int participationid;
  private String name;
  private String phoneNumber;
  private String village;
  private int age;
  private int gender; 

  // Database table
  public static final String PARTICIPANT_TABLE = "participants";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_PARTICIPATIONID = "_participationid";
  public static final String COLUMN_UPDATED = "updated"; //when this record was last modified
  public static final String COLUMN_NAME = "name"; //required
  public static final String COLUMN_PHONENUMBER = "phonenumber"; //optional
  public static final String COLUMN_VILLAGE = "village"; //optional
  public static final String COLUMN_AGE = "age"; //required
  public static final String COLUMN_GENDER = "sex"; //required

  // Database creation SQL statement
  private static final String DATABASE_CREATE = "create table if not exists "
          + PARTICIPANT_TABLE
          + "("
          + COLUMN_ID + " integer primary key autoincrement, "
          + COLUMN_UPDATED + " integer not null default (strftime('%s','now')), "
          + COLUMN_NAME + " string not null, "
          + COLUMN_PHONENUMBER + " integer, "
          + COLUMN_VILLAGE + " string, "
          + COLUMN_AGE + " integer not null, "
          + COLUMN_GENDER + " integer not null, "
          + COLUMN_PARTICIPATIONID + " integer not null references " + Participation.PARTICIPATION_TABLE + " (" + Participation.COLUMN_ID + ") ON DELETE CASCADE"
          + ");";

  // used to create the table
  public static void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  // used to upgrade the table
  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
          int newVersion) {
    database.execSQL("drop table if exists " + PARTICIPANT_TABLE);
    onCreate(database);
  }

  // Getters and Setters follow
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getParticipationid() {
    return participationid;
  }

  public void setParticipationid(int participationid) {
    this.participationid = participationid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getVillage() {
    return village;
  }

  public void setVillage(String village) {
    this.village = village;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeString(name);
    out.writeString(phoneNumber);
    out.writeString(village);
    out.writeInt(age);
    out.writeInt(gender);
  }

}
