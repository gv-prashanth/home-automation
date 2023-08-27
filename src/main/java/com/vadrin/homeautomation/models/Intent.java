package com.vadrin.homeautomation.models;

import java.util.Map;

public class Intent {

  private String intentName;
  private Map<String, String> info;

  public String getIntentName() {
    return intentName;
  }

  public void setIntentName(String intentName) {
    this.intentName = intentName;
  }

  public Map<String, String> getInfo() {
    return info;
  }

  public void setInfo(Map<String, String> info) {
    this.info = info;
  }

  public Intent(String intentName, Map<String, String> info) {
    super();
    this.intentName = intentName;
    this.info = info;
  }

}
