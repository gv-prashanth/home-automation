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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlexaService {

  private static final String GREET = "Hello! I am your droid ";
  private static final String ASK = "Which device information do you need?";
  private static final String HELP = "You can ask me about Water Tank, Solar Panel, Curtains, etc. " + ASK;
  private static final String BYE = "Bye Bye!";
  private static final String DONT_HAVE = "I dont have this information. ";
  private static final String NO_DROID = "You have a malfunctional droid! "+BYE;
  private static final String IS = " is ";
  private static final String REQUEST = "request";
  private static final String EMPTY = "";

  @Autowired
  DroidService droidService;

  @Autowired
  JsonService jsonService;

  public AlexaResponse respond(JsonNode alexaRequestBody) {
    log.debug("request is - " + alexaRequestBody.toString());
    String conversationId = alexaRequestBody.get("session").get("sessionId").asText();
    log.debug(conversationId);
    String requestType = alexaRequestBody.get(REQUEST).get("type").asText();
    String userId = alexaRequestBody.get("session").get("user").get("userId").asText();

    // Arrive at intent name using intentrequest object. if not possible arrive
    // using request type.
    String intentName = requestType.equalsIgnoreCase("IntentRequest")
        ? alexaRequestBody.get(REQUEST).get("intent").get("name").asText()
        : requestType;
    Map<String, String> slots = constructIntentSlots(alexaRequestBody);
    Intent intent = new Intent(intentName, slots);
    Response response = handleIntentRequest(userId, intent);
    AlexaResponse toReturn = constructAlexaResponse(response);
    log.debug("respose is - " + jsonService.getJson(toReturn).toString());
    return toReturn;
  }

  private Map<String, String> constructIntentSlots(JsonNode alexaRequestBody) {
    Map<String, String> eventSlots = new HashMap<>();
    try {
      alexaRequestBody.get(REQUEST).get("intent").get("slots").elements().forEachRemaining(child -> {
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
    Map<String, Object> speech = new HashMap<>();
    speech.put("type", "PlainText");
    speech.put("text", response.getMessage());

    Map<String, Object> card = new HashMap<>();
    card.put("type", "Standard");
    card.put("title", "My Droid");
    card.put("text", response.getMessage());

    Map<String, Object> image = new HashMap<>();
    image.put("smallImageUrl", "https://home-automation.vadrin.com/images/HomeDroid2Small.png");
    image.put("largeImageUrl", "https://home-automation.vadrin.com/images/HomeDroid2Medium.png");
    card.put("image", image);
    return new AlexaResponse("1.0", new HashMap<>(), new AlexaCardAndSpeech(speech, card, response.isTheEnd(), null));
  }

  private Response handleIntentRequest(String userId, Intent intent) {
    switch (intent.getIntentName()) {
    case "LaunchRequest":
      return handleLaunchIntent(userId);
    case "AMAZON.HelpIntent":
      return new Response(HELP, false);
    case "AMAZON.CancelIntent":
      return new Response(BYE, true);
    case "AMAZON.StopIntent":
      return new Response(BYE, true);
    case "AMAZON.FallbackIntent":
      return new Response(EMPTY, true);
    default:
      return handleAcceptedIntents(userId, intent);
    }
  }

  private Response handleAcceptedIntents(String userId, Intent intent) {
    try {
      Droid droid = droidService.getDroidForUser(userId);
      if (droid.getIntentsInfo().containsKey(intent.getIntentName()))
        return new Response(intent.getIntentName() + IS + droid.getIntentsInfo().get(intent.getIntentName()), false);
      else {
        // return new Response(DONT_HAVE + HELP, false);
        Random r = new Random();
        return new Response(intent.getIntentName() + IS + String.valueOf(r.nextInt(100)), false);
      }
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      return new Response(NO_DROID, true);
    } catch (FileNotFoundException e) {
      return new Response(NO_DROID, true);
    }
  }

  private Response handleLaunchIntent(String userId) {
    Droid droid;
    try {
      droid = droidService.getDroidForUser(userId);
    } catch (FileNotFoundException e) {
      try {
        droid = droidService.createNewDroid(userId);
      } catch (InterruptedException | ExecutionException e1) {
        Thread.currentThread().interrupt();
        return new Response(NO_DROID, true);
      }
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      return new Response(NO_DROID, true);
    }
    return new Response(
        GREET + Arrays.asList(droid.getDroidId().split("")).stream().collect(Collectors.joining(" ")) + ", " + ASK,
        false);
  }
}
