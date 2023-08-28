package com.vadrin.homeautomation.repositories;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class HomeRepository {
  
  private Map<String, String> userRepository = new HashMap<String, String>();
  
  public String getHomeId(String userId) {
    return userRepository.get(userId);
  }
  
  public boolean isRegistered(String userId) {
    return userRepository.containsKey(userId);
  }
  
  public void register(String userId, String homeId){
    userRepository.put(userId, homeId);
  }

}
