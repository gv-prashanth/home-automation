package com.vadrin.homeautomation.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vadrin.homeautomation.models.Droid;
import com.vadrin.homeautomation.services.DroidService;

@RestController
public class WebController {
  
  @Autowired
  DroidService droidService;

  @GetMapping("/droid/{id}/intents")
  public Map<String, String> getAllIntents(@PathVariable String id) {
    Optional<Droid> d = droidService.getDroid(id.toUpperCase());
    return d.isPresent()? d.get().getIntentsInfo() : new HashMap<>();
  }
  
  @GetMapping("/droid/create")
  public String createDroid(@RequestParam String userId) {
    return droidService.createNewDroid(userId).getDroidId();
  }
  
  @GetMapping("/droid/{id}/upsert/intent/{name}/reading/{reading}")
  public void upsertIntent(@PathVariable String id,@PathVariable String name,@PathVariable String reading) {
    System.out.println("intent request is - " + id + " " + name + " " + reading);
    droidService.upsertIntent(id.toUpperCase(), name, reading);
  }

}