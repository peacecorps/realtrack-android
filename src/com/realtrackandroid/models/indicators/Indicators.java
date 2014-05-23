package com.realtrackandroid.models.indicators;

public class Indicators {
    // Instance properties
    private String post; // which activity this reminder is for. This is a foreign key that points to Activities.id
    private String sector; // which activity this reminder is for. This is a foreign key that points to Activities.id
    private String project; // which activity this reminder is for. This is a foreign key that points to Activities.id
    private String goal; // which activity this reminder is for. This is a foreign key that points to Activities.id
    private String objective; // which activity this reminder is for. This is a foreign key that points to Activities.id
    private String indicator; // which activity this reminder is for. This is a foreign key that points to Activities.id

    // Database table
    public static final String INDICATORS_TABLE = "indicators";
    public static final String COLUMN_POST = "post";
    public static final String COLUMN_SECTOR = "sector";
    public static final String COLUMN_PROJECT = "project";
    public static final String COLUMN_GOAL = "goal";
    public static final String COLUMN_OBJECTIVE = "objective";
    public static final String COLUMN_INDICATOR = "indicator";
    
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
}
