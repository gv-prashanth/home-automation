package com.vadrin.homeautomation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vadrin.homeautomation.models.Intent;
import com.vadrin.homeautomation.models.Response;
import com.vadrin.homeautomation.repositories.IntentRepository;

@Service
public class ChatService {
  
  private static final String GREET = "Hello! Which device information would you like to know?";
  private static final String BYE = "Bye Bye!";
  private static final String DONT_HAVE = "Unfortunately, I dont have its information. Please try later.";
  private static final String IS = " is ";
  
  @Autowired
  IntentRepository intentRepository;
  
  public Response handleIntentRequest(String conversationId, Intent intent) {
    System.out.println(conversationId);
    switch (intent.getIntentName()) {
    case "LaunchRequest":
      return new Response(GREET, false);
    case "Default Welcome Intent":
      return new Response(GREET, false);
    case "AMAZON.HelpIntent":
      return new Response(GREET, false);
    case "AMAZON.CancelIntent":
      return new Response(BYE, true);
    case "AMAZON.StopIntent":
      return new Response(BYE, true);
    case "SessionEndedRequest":
      return new Response(BYE, true);
    default:
      String reading = intentRepository.getReading(intent.getIntentName());
      if (reading == null || reading.isBlank())
        return new Response(DONT_HAVE, true);
      else
        return new Response(
            intent.getIntentName() + IS + intentRepository.getReading(intent.getIntentName()), true);
    }
  }

}