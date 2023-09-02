package com.vadrin.homeautomation.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.vadrin.homeautomation.models.Droid;

@Service
public class DroidService {

  private Set<Droid> droids = new HashSet<>();

  public void upsertIntent(String droidId, String intentName, String intentReading) {
    getDroid(droidId).ifPresent(d -> d.getIntentsInfo().put(intentName, intentReading));
  }

  public Optional<Droid> getDroid(String droidId) {
    return droids.stream().filter(x -> x.getDroidId().equalsIgnoreCase(droidId)).findFirst();
  }
  
  public Optional<Droid> getDroidForUser(String userId) {
    return droids.stream().filter(x -> x.getUserId().equalsIgnoreCase(userId)).findFirst();
  }

  public Droid createNewDroid(String userId) {
    Random r = new Random();
    String droidId = String.valueOf((char)(r.nextInt(26) + 'A')) + String.valueOf(r.nextInt(10)) + String.valueOf((char)(r.nextInt(26) + 'A')) + String.valueOf(r.nextInt(10));
    Map<String, String> toPut = new HashMap<>();
    Droid toAdd = new Droid(droidId, userId, toPut);
    droids.add(toAdd);
    return toAdd;
  }

}
