package com.vadrin.homeautomation.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vadrin.homeautomation.repositories.IntentRepository;

@RestController
public class WebController {
  
  @Autowired
  IntentRepository intentRepository;

  @GetMapping("/intent/upsert")
  public void upsertReading(@RequestParam String name, @RequestParam String reading) {
    System.out.println("request is - " + name + " " + reading);
    intentRepository.upsertIntent(name, reading);
    ;
  }

  @GetMapping("/intents")
  public Map<String, String> intents() {
    System.out.println("request is - /intents");
    return intentRepository.getAllReadings();
  }

}