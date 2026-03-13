package com.vadrin.homeautomation.models;

import java.util.HashMap;
import java.util.Map;

public class DroidHistory {

  private String droidId;
  private Map<String, DeviceHistoryInfo> devices;

  public DroidHistory(String droidId, Map<String, DeviceHistoryInfo> devices) {
    this.droidId = droidId;
    this.devices = devices;
  }

  public DroidHistory() {
    this.devices = new HashMap<>();
  }

  public String getDroidId() {
    return droidId;
  }

  public void setDroidId(String droidId) {
    this.droidId = droidId;
  }

  public Map<String, DeviceHistoryInfo> getDevices() {
    return devices;
  }

  public void setDevices(Map<String, DeviceHistoryInfo> devices) {
    this.devices = devices;
  }

}
