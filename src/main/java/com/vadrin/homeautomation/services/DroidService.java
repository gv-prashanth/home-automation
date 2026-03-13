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
import java.time.Duration;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.vadrin.homeautomation.models.DeviceHistoryEntry;
import com.vadrin.homeautomation.models.DeviceHistoryInfo;
import com.vadrin.homeautomation.models.DeviceInfo;
import com.vadrin.homeautomation.models.Droid;
import com.vadrin.homeautomation.models.DroidHistory;


@Service
public class DroidService {
  
  
  @Autowired
  Firestore firestore;
  
  private Random r = new Random();
  
  private String[] attentionTerms = {"danger", "error", "shutdown", "dry run", "not receiving"};

  private static final long HISTORY_MIN_INTERVAL_SECONDS = 300;
  private static final long HISTORY_RETENTION_HOURS = 48;

  public void upsertDevice(String droidId, String deviceName, String deviceReading) throws InterruptedException, ExecutionException, FileNotFoundException {
    System.out.println("upsert request is - " + droidId + " " + deviceName + " " + deviceReading);
    Droid d = getDroid(droidId);
    String now = Instant.now().toString();
    d.getDevices().put(deviceName, new DeviceInfo(deviceReading, now, checkForAttention(deviceReading)));
    saveDroid(d);
    appendDeviceHistory(droidId, deviceName, deviceReading, now);
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

  public void deleteDevice(String droidId, String deviceName) throws FileNotFoundException, InterruptedException, ExecutionException {
	Droid thisDroid = getDroid(droidId);
	thisDroid.getDevices().remove(deviceName);
	saveDroid(thisDroid);
  }

  private void appendDeviceHistory(String droidId, String deviceName, String deviceReading, String readingTime) {
    try {
      DroidHistory history = getDroidHistory(droidId);
      DeviceHistoryInfo deviceHistory = history.getDevices().get(deviceName);
      if (deviceHistory == null) {
        deviceHistory = new DeviceHistoryInfo();
        history.getDevices().put(deviceName, deviceHistory);
      }

      List<DeviceHistoryEntry> entries = deviceHistory.getDeviceReadingHistory();
      if (!entries.isEmpty()) {
        DeviceHistoryEntry lastEntry = entries.get(entries.size() - 1);
        boolean sameValue = deviceReading.equals(lastEntry.getDeviceReading());
        boolean tooSoon = Duration.between(
            Instant.parse(lastEntry.getReadingTime()), Instant.parse(readingTime)
        ).getSeconds() < HISTORY_MIN_INTERVAL_SECONDS;

        if (sameValue && tooSoon) {
          return;
        }
      }

      entries.add(new DeviceHistoryEntry(deviceReading, readingTime));
      pruneOldEntries(history);
      saveDroidHistory(history);
    } catch (Exception e) {
      System.out.println("Failed to append device history: " + e.getMessage());
    }
  }

  private void pruneOldEntries(DroidHistory history) {
    Instant cutoff = Instant.now().minus(Duration.ofHours(HISTORY_RETENTION_HOURS));
    for (DeviceHistoryInfo deviceHistory : history.getDevices().values()) {
      Iterator<DeviceHistoryEntry> it = deviceHistory.getDeviceReadingHistory().iterator();
      while (it.hasNext()) {
        DeviceHistoryEntry entry = it.next();
        if (Instant.parse(entry.getReadingTime()).isBefore(cutoff)) {
          it.remove();
        }
      }
    }
  }

  public List<DeviceHistoryEntry> getDeviceHistory(String droidId, String deviceName, int hours) throws InterruptedException, ExecutionException {
    DroidHistory history = getDroidHistory(droidId);
    DeviceHistoryInfo deviceHistory = history.getDevices().get(deviceName);
    if (deviceHistory == null) {
      return new ArrayList<>();
    }
    Instant cutoff = Instant.now().minus(Duration.ofHours(hours));
    return deviceHistory.getDeviceReadingHistory().stream()
        .filter(e -> Instant.parse(e.getReadingTime()).isAfter(cutoff))
        .collect(Collectors.toList());
  }

  private DroidHistory getDroidHistory(String droidId) throws InterruptedException, ExecutionException {
    ApiFuture<DocumentSnapshot> documentFuture = this.firestore.document("DroidHistoryRepository/" + droidId).get();
    DroidHistory history = documentFuture.get().toObject(DroidHistory.class);
    if (history == null) {
      history = new DroidHistory();
      history.setDroidId(droidId);
    }
    return history;
  }

  private void saveDroidHistory(DroidHistory history) throws InterruptedException, ExecutionException {
    this.firestore.document("DroidHistoryRepository/" + history.getDroidId()).set(history).get();
  }

}
