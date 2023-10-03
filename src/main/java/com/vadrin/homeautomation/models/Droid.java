package com.vadrin.homeautomation.models;

import java.util.Map;

public class Droid {

  private String droidId;
  private String userId;
  private Map<String, DeviceInfo> devices;

  public String getDroidId() {
    return droidId;
  }

  public void setDroidId(String id) {
    this.droidId = id;
  }

  public Map<String, DeviceInfo> getDevices() {
    return devices;
  }

  public void setDevices(Map<String, DeviceInfo> devices) {
    this.devices = devices;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Droid(String droidId, String userId, Map<String, DeviceInfo> devices) {
    super();
    this.droidId = droidId;
    this.userId = userId;
    this.devices = devices;
  }

  public Droid() {
    super();
  }

}
