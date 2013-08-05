package com.hackforchange.views.participationsummary;

import java.util.List;

class ProjectHolder {
  String getTitle() {
    return title;
  }

  void setTitle(String title) {
    this.title = title;
  }

  List<ActivitiesHolder> getActivitiesHolderList() {
    return activitiesHolderList;
  }

  void setActivitiesHolderList(List<ActivitiesHolder> activitiesHolderList) {
    this.activitiesHolderList = activitiesHolderList;
  }

  String title;
  List<ActivitiesHolder> activitiesHolderList;
}

