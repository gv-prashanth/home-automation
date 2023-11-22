package com.vadrin.homeautomation.services;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.vadrin.homeautomation.models.DeviceInfo;
import com.vadrin.homeautomation.models.Droid;


@Service
public class DroidService {
  
  
  @Autowired
  Firestore firestore;
  
  private Random r = new Random();
  
  private String[] attentionTerms = {"danger", "error", "shutdown", "dry run", "not receiving"};

  public void upsertDevice(String droidId, String deviceName, String deviceReading) throws InterruptedException, ExecutionException, FileNotFoundException {
    System.out.println("upsert request is - " + droidId + " " + deviceName + " " + deviceReading);
    Droid d = getDroid(droidId);
    d.getDevices().put(deviceName, new DeviceInfo(deviceReading, Instant.now().toString(), checkForAttention(deviceReading)));
    saveDroid(d);
  }

  private boolean checkForAttention(String deviceReading) {
    return Arrays.asList(attentionTerms).stream().anyMatch(x -> deviceReading.toLowerCase().contains(x.toLowerCase()));
  }

  public Droid getDroid(String droidId) throws InterruptedException, ExecutionException, FileNotFoundException {
    ApiFuture<DocumentSnapshot> documentFuture = this.firestore.document("DroidRepository/"+droidId).get();
    Droid droid = documentFuture.get().toObject(Droid.class);
    if(droid == null)
      throw new FileNotFoundException();
    else {
      //Add the two default devices for every droid. Clock & Calendar
      //droid.getDevices().put("Clock", new DeviceInfo("at "+Instant.now().atZone(ZoneId.of("Asia/Kolkata")).toLocalTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)), Instant.now().toString()));
      //droid.getDevices().put("Calendar", new DeviceInfo("at "+Instant.now().atZone(ZoneId.of("Asia/Kolkata")).toLocalDate().format(DateTimeFormatter.ofPattern("E, MMM d, y")), Instant.now().toString()));
          
      // Sort the LinkedHashMap by readingTime
      List<Map.Entry<String, DeviceInfo>> sortedList = new ArrayList<>(droid.getDevices().entrySet());
      //Collections.sort(sortedList, (entry1, entry2) -> Instant.parse(entry2.getValue().getReadingTime()).compareTo(Instant.parse((entry1.getValue().getReadingTime()))));
      Collections.sort(sortedList, (entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()));
      // Create a new LinkedHashMap with the sorted entries
      Map<String, DeviceInfo> sortedLinkedHashMap = new LinkedHashMap<>();
      for (Map.Entry<String, DeviceInfo> entry : sortedList) {
        sortedLinkedHashMap.put(entry.getKey(), entry.getValue());
      }
      
      droid.setDevices(sortedLinkedHashMap);
      return droid;
    }
  }
  
  public Droid getDroidForUser(String userId) throws InterruptedException, ExecutionException, FileNotFoundException {
    ApiFuture<QuerySnapshot> queryFuture = this.firestore.collection("DroidRepository").whereEqualTo("userId", userId).get();
    List<QueryDocumentSnapshot> queryDocuments = queryFuture.get().getDocuments();
    Optional<Droid> toReturn = queryDocuments.stream().map(x -> x.toObject(Droid.class)).findAny();
    if(toReturn.isPresent())
      return toReturn.get();
    else
      throw new FileNotFoundException();
  }

  public Droid createNewDroid(String userId) throws InterruptedException, ExecutionException {
    String droidId = String.valueOf((char)(r.nextInt(26) + 'A')) + String.valueOf(r.nextInt(10)) + String.valueOf((char)(r.nextInt(26) + 'A')) + String.valueOf(r.nextInt(10));
    Map<String, DeviceInfo> toPut = new HashMap<>();
    Droid toAdd = new Droid(droidId, userId, toPut);
    saveDroid(toAdd);
    return toAdd;
  }

  private void saveDroid(Droid toAdd) throws InterruptedException, ExecutionException {
    this.firestore.document("DroidRepository/"+toAdd.getDroidId()).set(toAdd).get();
  }
  
}
