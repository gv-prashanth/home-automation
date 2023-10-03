package com.vadrin.homeautomation.controllers;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vadrin.homeautomation.models.DeviceInfo;
import com.vadrin.homeautomation.models.Droid;
import com.vadrin.homeautomation.services.DroidService;

@RestController
public class WebController {
  
  @Autowired
  DroidService droidService;

  @GetMapping("/droid/{droidId}/intents")
  public Map<String, DeviceInfo> getAllIntents(@PathVariable String droidId) {
    try {
      Droid d = droidService.getDroid(droidId.toUpperCase());
      return d.getDevices();
    } catch (InterruptedException | ExecutionException | FileNotFoundException e) {
      Thread.currentThread().interrupt();
      return new HashMap<String, DeviceInfo>();
    }
  }
  
  @GetMapping("/droid/create")
  public String createDroid(@RequestParam String userId) throws InterruptedException, ExecutionException {
    return droidService.createNewDroid(userId).getDroidId();
  }
  
  @GetMapping("/droid/{droidId}/upsert/intent/{intentName}/reading/{intentReading}")
  public void upsertIntent(@PathVariable String droidId,@PathVariable String intentName,@PathVariable String intentReading) throws FileNotFoundException, InterruptedException, ExecutionException {
    droidService.upsertIntent(droidId.toUpperCase(), intentName, intentReading);
  }

}