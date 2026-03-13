package com.vadrin.homeautomation.models;

public class DeviceHistoryEntry {

  private String deviceReading;
  private String readingTime;

  public DeviceHistoryEntry(String deviceReading, String readingTime) {
    this.deviceReading = deviceReading;
    this.readingTime = readingTime;
  }

  public DeviceHistoryEntry() {
  }

  public String getDeviceReading() {
    return deviceReading;
  }

  public void setDeviceReading(String deviceReading) {
    this.deviceReading = deviceReading;
  }

  public String getReadingTime() {
    return readingTime;
  }

  public void setReadingTime(String readingTime) {
    this.readingTime = readingTime;
  }

}
