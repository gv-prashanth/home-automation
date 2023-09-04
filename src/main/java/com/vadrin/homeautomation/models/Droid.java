package com.vadrin.homeautomation.models;

import java.util.Map;

public class Droid {

  private String droidId;
  private String userId;
  private Map<String, String> intentsInfo;

  public String getDroidId() {
    return droidId;
  }

  public void setDroidId(String id) {
    this.droidId = id;
  }

  public Map<String, String> getIntentsInfo() {
    return intentsInfo;
  }

  public void setIntentsInfo(Map<String, String> intentsInfo) {
    this.intentsInfo = intentsInfo;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Droid(String droidId, String userId, Map<String, String> intentsInfo) {
    super();
    this.droidId = droidId;
    this.userId = userId;
    this.intentsInfo = intentsInfo;
  }

  public Droid() {
    super();
  }

}
