package com.vadrin.homeautomation.repositories;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class IntentRepository {
  
  private Map<String, Map<String, String>> intentRepository;
  
  public String getReading(String homeName, String intentName) {
    return intentRepository.get(homeName).get(intentName);
  }
  
  public void upsertIntent(String homeName, String intentName, String intentReading) {
    if(intentRepository.containsKey(homeName)) {
      intentRepository.get(homeName).put(intentName, intentReading);
    }else {
      Map<String, String> toPut = new HashMap<>();
      toPut.put(intentName, intentReading);
      intentRepository.put(homeName, toPut);
    }
  }
  
  public Map<String, String> getAllReadings(String homeName){
    return intentRepository.get(homeName);
  }
  
  @PostConstruct
  protected void initializeRepo() {
    intentRepository = new HashMap<>();
  }

}
