package com.hackforchange.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * This class receives the boot completed event whenever the phone is started up
 * It's job is to schedule an alarm for this app's notifications
 */
public class NotificationReceiver extends BroadcastReceiver {
  public void onReceive(Context context, Intent intent) {
    // in our case intent will always be BOOT_COMPLETED, so we can just set
    // the alarm
    // Note that a BroadcastReceiver is *NOT* a Context. Thus, we can't use
    // "this" whenever we need to pass a reference to the current context.
    // Thankfully, Android will supply a valid Context as the first parameter

    // schedule an alarm
    scheduleAlarm(context);
  }

  public static void scheduleAlarm(Context context) {
    Intent myIntent = new Intent(context, NotificationService.class);
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, 0);
    alarmManager.cancel(pendingIntent);

    Calendar calendar = Calendar.getInstance();
    Calendar c = Calendar.getInstance();
    //TODO: read in time from reminders table and convert to calendar object
    //c.set(Calendar.HOUR, date.getHours());
    //c.set(Calendar.MINUTE,date.getMinutes());

    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
  }
}
