package com.vadrin.homeautomation.models;

import java.util.ArrayList;
import java.util.List;

public class DeviceHistoryInfo {

  private List<DeviceHistoryEntry> deviceReadingHistory;

  public DeviceHistoryInfo(List<DeviceHistoryEntry> deviceReadingHistory) {
    this.deviceReadingHistory = deviceReadingHistory;
  }

  public DeviceHistoryInfo() {
    this.deviceReadingHistory = new ArrayList<>();
  }

  public List<DeviceHistoryEntry> getDeviceReadingHistory() {
    return deviceReadingHistory;
  }

  public void setDeviceReadingHistory(List<DeviceHistoryEntry> deviceReadingHistory) {
    this.deviceReadingHistory = deviceReadingHistory;
  }

}
