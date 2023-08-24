package com.vadrin.homeautomation.repositories;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class DevicesRepository {
  
  private Map<String, String> devicesRepository = new HashMap<String, String>();
  
  public String getDeviceReading(String deviceName) {
    return devicesRepository.get(deviceName);
  }
  
  public Map<String, String> getAllDeviceReadings() {
    return devicesRepository;
  }
  
  public void upsertDeviceReading(String deviceName, String reading) {
    devicesRepository.put(deviceName, reading);
  }

}
