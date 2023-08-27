package com.vadrin.homeautomation.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.vadrin.homeautomation.repositories.IntentRepository;

@RestController
public class WebController {
  
  @Autowired
  IntentRepository intentRepository;

  @GetMapping("/home/{home}/intents")
  public Map<String, String> getAllIntents(@PathVariable String home) {
    return intentRepository.getAllReadings(home);
  }
  
  @GetMapping("/home/{home}/upsert/intent/{name}/reading/{reading}")
  public void upsertIntent(@PathVariable String home,@PathVariable String name,@PathVariable String reading) {
    System.out.println("request is - " + home + " " + name + " " + reading);
    intentRepository.upsertIntent(home, name, reading);
  }

}