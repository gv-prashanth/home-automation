package com.vadrin.homeautomation.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vadrin.homeautomation.repositories.DevicesRepository;

@RestController
public class WebController {

  @Autowired
  private DevicesRepository devicesRepository;

  @GetMapping("/device/upsertReading")
  public void upsertReading(@RequestParam String deviceName, @RequestParam String deviceReading) {
    System.out.println("request is - " + deviceName + " " + deviceReading);
    devicesRepository.upsertDeviceReading(deviceName, deviceReading);
    ;
  }

  @GetMapping("/devices")
  public Map<String, String> devices() {
    System.out.println("request is - /devices");
    return devicesRepository.getAllDeviceReadings();
  }

}