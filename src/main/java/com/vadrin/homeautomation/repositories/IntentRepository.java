package com.vadrin.homeautomation.repositories;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class IntentRepository {
  
  private Map<String, String> intentRepository = new HashMap<String, String>();
  
  public String getReading(String intentName) {
    return intentRepository.get(intentName);
  }
  
  public String upsertIntent(String intentName, String intentReading) {
    return intentRepository.put(intentName, intentReading);
  }
  
  public Map<String, String> getAllReadings(){
    return intentRepository;
  }
  
}
