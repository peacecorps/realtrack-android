package com.realtrackandroid.views.participationsdonesummaries;

import android.os.AsyncTask;

public class SendDataTask extends AsyncTask<Void, Void, Boolean> {
  ParticipationSummaryActivity participationSummaryActivity;

  String dataFileName;

  String participationFileName;

  String signInReportsFileName;

  boolean use_email_not_bt;

  public SendDataTask(ParticipationSummaryActivity participationSummaryActivity) {
    this.participationSummaryActivity = participationSummaryActivity;
  }

  public void reAttach(ParticipationSummaryActivity participationSummaryActivity) {
    this.participationSummaryActivity = participationSummaryActivity;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
  }

  @Override
  protected Boolean doInBackground(final Void... params) {
    participationSummaryActivity.prepareDataInBackgroundCallback();
    return true;
  }

  @Override
  protected void onPostExecute(final Boolean success) {
    super.onPostExecute(success);
    participationSummaryActivity.shareDataCallback();
  }
}
