package com.vadrin.homeautomation.services;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vadrin.homeautomation.models.Droid;
import com.vadrin.homeautomation.models.Intent;
import com.vadrin.homeautomation.models.Response;
import com.vadrin.homeautomation.models.alexa.AlexaCardAndSpeech;
import com.vadrin.homeautomation.models.alexa.AlexaResponse;

@Service
public class AlexaService {

  private static final String GREET = "Hello! I am your dedicated droid ";
  private static final String ASK = "Which information do you need?";
  private static final String HELP = "You can ask me about Water Tank, Solar Panel, Curtains, etc. " + ASK;
  private static final String BYE = "Bye Bye!";
  private static final String DONT_HAVE = "Sorry. I dont have this information. Please try later.";
  private static final String NO_DROID = "Sorry. We have a malfunctional droid! Please try later.";
  private static final String IS = " is ";

  @Autowired
  DroidService droidService;

  public AlexaResponse respond(JsonNode alexaRequestBody) {
    //String conversationId = alexaRequestBody.get("session").get("sessionId").asText();
    String requestType = alexaRequestBody.get("request").get("type").asText();
    String userId = alexaRequestBody.get("session").get("user").get("userId").asText();

    // Arrive at intent name using intentrequest object. if not possible arrive
    // using request type.
    String intentName = requestType.equalsIgnoreCase("IntentRequest")
        ? alexaRequestBody.get("request").get("intent").get("name").asText()
        : requestType;
    Map<String, String> slots = constructIntentSlots(alexaRequestBody);
    Intent intent = new Intent(intentName, slots);
    Response response = handleIntentRequest(userId, intent);
    AlexaResponse toReturn = constructAlexaResponse(response);
    System.out.println("respose is - " + JsonService.getJson(toReturn).toString());
    return toReturn;
  }

  private Map<String, String> constructIntentSlots(JsonNode alexaRequestBody) {
    Map<String, String> eventSlots = new HashMap<String, String>();
    try {
      alexaRequestBody.get("request").get("intent").get("slots").elements().forEachRemaining(child -> {
        try {
          eventSlots.put(child.get("name").asText(), child.get("resolutions").get("resolutionsPerAuthority").get(0)
              .get("values").get(0).get("value").get("name").asText());
        } catch (NullPointerException e) {
          eventSlots.put(child.get("name").asText(), child.get("value").asText());
        }
      });
    } catch (NullPointerException e) {
      // No params at all
    }
    return eventSlots;
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
    return toReturn;
  }

  private Response handleIntentRequest(String userId, Intent intent) {
    switch (intent.getIntentName()) {
    case "LaunchRequest": {
      Droid droid;
      try {
        droid = droidService.getDroidForUser(userId);
      } catch (FileNotFoundException e) {
        try {
          droid = droidService.createNewDroid(userId);
        } catch (InterruptedException | ExecutionException e1) {
          return new Response(NO_DROID, true);
        }
      } catch (InterruptedException | ExecutionException e) {
        return new Response(NO_DROID, true);
      }
      return new Response(
          GREET + Arrays.asList(droid.getDroidId().split("")).stream().collect(Collectors.joining(" ")) + ", " + ASK,
          false);
    }
    case "AMAZON.HelpIntent":
      return new Response(HELP, false);
    case "AMAZON.CancelIntent":
      return new Response(BYE, true);
    case "AMAZON.StopIntent":
      return new Response(BYE, true);
    default: {
      try {
        Droid droid = droidService.getDroidForUser(userId);
        if (droid.getIntentsInfo().containsKey(intent.getIntentName()))
          return new Response(intent.getIntentName() + IS + droid.getIntentsInfo().get(intent.getIntentName()), true);
        else {
          //return new Response(DONT_HAVE, true);
          Random r = new Random();
          return new Response(intent.getIntentName() + IS + String.valueOf(r.nextInt(100)), true);
        }
      } catch (InterruptedException | ExecutionException | FileNotFoundException e) {
        return new Response(NO_DROID, true);
      }
    }
    }
  }
}
