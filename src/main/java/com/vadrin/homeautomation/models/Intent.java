package com.vadrin.homeautomation.models;

import java.util.Map;

public class Intent {

  private String intentName;
  private Map<String, String> slots;

  public String getIntentName() {
    return intentName;
  }

  public void setIntentName(String intentName) {
    this.intentName = intentName;
  }

  public Map<String, String> getSlots() {
    return slots;
  }

  public void setSlots(Map<String, String> slots) {
    this.slots = slots;
  }

  public Intent(String intentName, Map<String, String> slots) {
    super();
    this.intentName = intentName;
    this.slots = slots;
  }

}
