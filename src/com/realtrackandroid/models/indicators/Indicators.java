package com.realtrackandroid.models.indicators;

public class Indicators {
  // Instance properties
  private String post;

  private String sector;

  private String project;

  private String goal;

  private String objective;

  private String indicator;

  private String type;

  // Database table
  public static final String INDICATORS_TABLE = "indicators";

  public static final String COLUMN_POST = "post";

  public static final String COLUMN_SECTOR = "sector";

  public static final String COLUMN_PROJECT = "project";

  public static final String COLUMN_GOAL = "goal";

  public static final String COLUMN_OBJECTIVE = "objective";

  public static final String COLUMN_INDICATOR = "indicator";

  public static final String COLUMN_TYPE = "type";

  // Getters and Setters follow
  public Indicators() {
    super();
  }

  public String getPost() {
    return post;
  }

  public void setPost(String post) {
    this.post = post;
  }

  public String getSector() {
    return sector;
  }

  public void setSector(String sector) {
    this.sector = sector;
  }

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public String getGoal() {
    return goal;
  }

  public void setGoal(String goal) {
    this.goal = goal;
  }

  public String getObjective() {
    return objective;
  }

  public void setObjective(String objective) {
    this.objective = objective;
  }

  public String getIndicator() {
    return indicator;
  }

  public void setIndicator(String indicator) {
    this.indicator = indicator;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
