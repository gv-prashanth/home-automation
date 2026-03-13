package com.vadrin.homeautomation.controllers;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vadrin.homeautomation.models.DeviceHistoryEntry;
import com.vadrin.homeautomation.models.DeviceInfo;
import com.vadrin.homeautomation.models.Droid;
import com.vadrin.homeautomation.services.DroidService;


@RestController
public class WebController {
  
  @Autowired
  DroidService droidService;

  //TODO: Someday I need to change this url to /devices
  @GetMapping("/droid/{droidId}/intents")
  public Map<String, DeviceInfo> getAllDevices(@PathVariable String droidId) {
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
  
  //TODO: Someday I need to change this url to upsert/device/
  @GetMapping("/droid/{droidId}/upsert/intent/{deviceName}/reading/{deviceReading}")
  public void upsertDevice(@PathVariable String droidId,@PathVariable String deviceName,@PathVariable String deviceReading) throws FileNotFoundException, InterruptedException, ExecutionException {
    droidService.upsertDevice(droidId.toUpperCase(), deviceName, deviceReading);
  }
  
  @GetMapping("/droid/{droidId}/devices/{deviceName}")
  public DeviceInfo getDevice(@PathVariable String droidId, @PathVariable String deviceName) throws FileNotFoundException, InterruptedException, ExecutionException {
    Droid d = droidService.getDroid(droidId.toUpperCase());
    DeviceInfo info = d.getDevices().get(deviceName);
    return info;
  }

  @DeleteMapping("/droid/{droidId}/devices/{deviceName}")
  public void deleteDevice(@PathVariable String droidId,@PathVariable String deviceName) throws FileNotFoundException, InterruptedException, ExecutionException {
    droidService.deleteDevice(droidId.toUpperCase(), deviceName);
  }

  @GetMapping("/droid/{droidId}/devices/{deviceName}/history")
  public List<DeviceHistoryEntry> getDeviceHistory(@PathVariable String droidId, @PathVariable String deviceName, @RequestParam(defaultValue = "24") int hours) {
    try {
      return droidService.getDeviceHistory(droidId.toUpperCase(), deviceName, hours);
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      return new ArrayList<>();
    }
  }

}