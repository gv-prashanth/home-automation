package com.vadrin.homeautomation.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vadrin.homeautomation.models.Intent;
import com.vadrin.homeautomation.models.Response;
import com.vadrin.homeautomation.models.alexa.AlexaCardAndSpeech;
import com.vadrin.homeautomation.models.alexa.AlexaResponse;
import com.vadrin.homeautomation.repositories.IntentRepository;
import com.vadrin.homeautomation.repositories.UserRepository;

@Service
public class AlexaService {
	
  private static final String GREET = "Hello. I can help you with your home devices.";
  private static final String GREETLONG = GREET+" Which device information do you need?";
  private static final String HELP = "You can ask me about Water Tank, Solar Panel, Curtains, etc.";
  private static final String BYE = "Bye Bye!";
  private static final String DONT_HAVE = "Unfortunately, I dont have its information. Please try later.";
  private static final String IS = " is ";
  private static final String PREPARE_HOME = "Thanks. Give me couple of minutes to prepare your home";
  private static final String TELL_HOME = "Your home is ";
  
  @Autowired
  IntentRepository intentRepository;
  
  @Autowired
  UserRepository userRepository;
  
	public AlexaResponse respond(JsonNode alexaRequestBody) {
    String conversationId = alexaRequestBody.get("session").get("sessionId").asText();
	  String requestType = alexaRequestBody.get("request").get("type").asText();
	  String userId = alexaRequestBody.get("session").get("user").get("userId").asText();

    //Arrive at intent name using intentrequest object. if not possible arrive using request type.
    String intentName = requestType.equalsIgnoreCase("IntentRequest") ? alexaRequestBody.get("request").get("intent").get("name").asText() : requestType;
    Map<String, String> intentParams = constructIntentParams(alexaRequestBody);
    Intent intent = new Intent(intentName, intentParams, userId);
    Response response = handleIntentRequest(conversationId, intent);
    AlexaResponse toReturn =  constructAlexaResponse(response);
    if (requestType.equalsIgnoreCase("LaunchRequest")) {
      if(!userRepository.isRegistered(userId)) {
        addRedirectToCreateHomeIntent(toReturn, "CreateHome");
      }
    }
    return toReturn;
	}
	
  private void addRedirectToCreateHomeIntent(AlexaResponse toReturn, String intentToRedirect) {
    List<Map<String, Object>> directives = new ArrayList<Map<String, Object>>();
    Map<String, Object> updateIntent = new HashMap<String, Object>();
    updateIntent.put("type", "Dialog.Delegate");
    updateIntent.put("updatedIntent", intentToRedirect);
    directives.add(updateIntent);
    toReturn.getResponse().setDirectives(directives);
  }

  private Map<String, String> constructIntentParams(JsonNode alexaRequestBody) {
    Map<String, String> eventParams = new HashMap<String, String>();
    try {
      alexaRequestBody.get("request").get("intent").get("slots").elements().forEachRemaining(child -> {
        try {
          eventParams.put(child.get("name").asText(), child.get("resolutions").get("resolutionsPerAuthority")
              .get(0).get("values").get(0).get("value").get("name").asText());
        } catch (NullPointerException e) {
          eventParams.put(child.get("name").asText(), child.get("value").asText());
        }
      });
    } catch (NullPointerException e) {
      //No params at all
    }
    return eventParams;
	}

	private AlexaResponse constructAlexaResponse(Response response) {
    Map<String, Object> speech = new HashMap<String, Object>();
    speech.put("type", "PlainText");
    speech.put("text", response.getMessage());

    Map<String, Object> card = new HashMap<String, Object>();
    card.put("type", "Standard");
    card.put("title", "My Droid");
    card.put("text", response.getMessage());

    Map<String, Object> image = new HashMap<String, Object>();
    image.put("smallImageUrl", "https://home-automation.vadrin.com/images/HomeDroid2Small.png");
    image.put("largeImageUrl", "https://home-automation.vadrin.com/images/HomeDroid2Medium.png");
    card.put("image", image);
    AlexaResponse toReturn = new AlexaResponse("1.0", new HashMap<String, Object>(),
        new AlexaCardAndSpeech(speech, card, response.isTheEnd(), null));
    System.out.println("respose is - " + JsonService.getJson(toReturn).toString());
    return toReturn;
	}
  
  private Response handleIntentRequest(String conversationId, Intent intent) {
    System.out.println(conversationId);
    switch (intent.getIntentName()) {
    case "LaunchRequest":{
      if (userRepository.isRegistered(intent.getUserId()))
        return new Response(GREETLONG, false);
      else
        return new Response(GREET, false);
    }case "AMAZON.HelpIntent":
      return new Response(HELP, false);
    case "AMAZON.CancelIntent":
      return new Response(BYE, true);
    case "AMAZON.StopIntent":
      return new Response(BYE, true);
    case "WhatIsMyHome":
      return new Response(TELL_HOME + userRepository.getHome(intent.getUserId()), true);
    case "CreateHome": {
      userRepository.register(intent.getUserId(), intent.getInfo().get("homeName"));
      return new Response(PREPARE_HOME, true);
    }
    default:
      String reading = intentRepository.getReading(userRepository.getHome(intent.getUserId()), intent.getIntentName());
      if (reading == null || reading.isBlank())
        return new Response(DONT_HAVE, true);
      else
        return new Response(
            intent.getIntentName() + IS
                + intentRepository.getReading(userRepository.getHome(intent.getUserId()), intent.getIntentName()),
            true);
    }
  }

}
