package com.vadrin.homeautomation.models;

import java.time.Instant;

public class DeviceInfo {

  private String deviceReading;
  private String readingTime;

  public String getDeviceReading() {
    return deviceReading;
  }

  public void setDeviceReading(String deviceReading) {
    this.deviceReading = deviceReading;
  }

  public DeviceInfo(String deviceReading, String readingTime) {
    super();
    this.deviceReading = deviceReading;
    this.readingTime = readingTime;
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
