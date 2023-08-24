package com.vadrin.homeautomation.repositories;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class IntentsRepository {
  
  private Map<String, String> intentssRepository = new HashMap<String, String>();
  
  public String getDeviceName(String intentName) {
    intentssRepository.put("WaterLevelIntent", "WaterTank");
    return intentssRepository.get(intentName);
  }
  
}
