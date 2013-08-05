package com.hackforchange.views.participationsummary;

import com.hackforchange.models.activities.Participation;

import java.util.List;

public class ActivitiesHolder {
  String title;

  String getTitle() {
    return title;
  }

  void setTitle(String title) {
    this.title = title;
  }

  List<Participation> getParticipationList() {
    return participationList;
  }

  void setParticipationList(List<Participation> participationList) {
    this.participationList = participationList;
  }

  List<Participation> participationList;
}