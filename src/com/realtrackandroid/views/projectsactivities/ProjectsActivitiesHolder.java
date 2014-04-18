package com.realtrackandroid.views.projectsactivities;

import java.util.List;

import com.realtrackandroid.models.activities.Activities;
import com.realtrackandroid.models.projects.Project;

class ProjectsActivitiesHolder {
  Project project;
  List<Activities> activitiesList;

  Project getProject() {
    return project;
  }

  void setProject(Project project) {
    this.project = project;
  }

  List<Activities> getActivitiesList() {
    return activitiesList;
  }

  void setActivitiesList(List<Activities> activitiesList) {
    this.activitiesList = activitiesList;
  }
}