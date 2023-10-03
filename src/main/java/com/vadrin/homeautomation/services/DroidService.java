package com.vadrin.homeautomation.services;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.HashMap;
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

  public void upsertIntent(String droidId, String intentName, String intentReading) throws InterruptedException, ExecutionException, FileNotFoundException {
    System.out.println("upsert request is - " + droidId + " " + intentName + " " + intentReading);
    Droid d = getDroid(droidId);
    d.getDevices().put(intentName, new DeviceInfo(intentReading, Instant.now().toString()));
    saveDroid(d);
  }

  public Droid getDroid(String droidId) throws InterruptedException, ExecutionException, FileNotFoundException {
    ApiFuture<DocumentSnapshot> documentFuture = this.firestore.document("DroidRepository/"+droidId).get();
    Droid droid = documentFuture.get().toObject(Droid.class);
    if(droid == null)
      throw new FileNotFoundException();
    else
      return droid;
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
