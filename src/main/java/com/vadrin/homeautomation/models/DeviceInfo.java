package com.vadrin.homeautomation.models;

public class DeviceInfo {

  private String deviceReading;
  private String readingTime;
  private boolean inNeedOfAttention;

  public boolean isInNeedOfAttention() {
    return inNeedOfAttention;
  }

  public void setInNeedOfAttention(boolean inNeedOfAttention) {
    this.inNeedOfAttention = inNeedOfAttention;
  }

  public String getDeviceReading() {
    return deviceReading;
  }

  public void setDeviceReading(String deviceReading) {
    this.deviceReading = deviceReading;
  }

  public DeviceInfo(String deviceReading, String readingTime, boolean inNeedOfAttention) {
    super();
    this.deviceReading = deviceReading;
    this.readingTime = readingTime;
    this.inNeedOfAttention = inNeedOfAttention;
  }

  public String getReadingTime() {
    return readingTime;
  }

  public void setReadingTime(String readingTime) {
    this.readingTime = readingTime;
  }

  public DeviceInfo() {
    super();
  }

}
