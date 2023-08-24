package com.vadrin.homeautomation.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vadrin.homeautomation.models.AlexaCardAndSpeech;
import com.vadrin.homeautomation.models.AlexaResponse;
import com.vadrin.homeautomation.models.Chat;
import com.vadrin.homeautomation.models.Event;
import com.vadrin.homeautomation.repositories.DevicesRepository;
import com.vadrin.homeautomation.repositories.IntentsRepository;

@Service
public class AlexaService {
  
  @Autowired
  DevicesRepository devicesRepository;
  
  @Autowired
  IntentsRepository intentsRepository;
	
  private static final String GREET = "Hello! Welcome to Vadrin! What would you like to know?";
  private static final String BYE = "Bye Bye!";
  private static final String TROUBLE_UNDERSTANDING = "I am having trouble understanding you. Please try later.";
  private static final String DONT_HAVE = "Unfortunately, I dont have its information. Please try later.";
  private static final String IS = " is ";

	public AlexaResponse respond(JsonNode alexaRequestBody) {
		if (alexaRequestBody.get("request").has("dialogState")
				&& !alexaRequestBody.get("request").get("dialogState").asText().equalsIgnoreCase("COMPLETED")) {
			return autoFetchSlots(alexaRequestBody);
		}
		if (alexaRequestBody.get("request").get("type").asText().equalsIgnoreCase("IntentRequest")) {
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
			}catch(NullPointerException e) {
			 //this intent has no slots
			  System.out.println("this intent has no slots");
			}
			Event input = new Event(alexaRequestBody.get("request").get("intent").get("name").asText(), eventParams);
			Chat output = handle(alexaRequestBody.get("session").get("sessionId").asText(), input);
			return constructAlexaResponse(output);
		} else {
			Event input = new Event(alexaRequestBody.get("request").get("type").asText());
			Chat output = handle(alexaRequestBody.get("session").get("sessionId").asText(), input);
			return constructAlexaResponse(output);
		}
	}

	private AlexaResponse constructAlexaResponse(String response, boolean endSession) {
		Map<String, Object> speech = new HashMap<String, Object>();
		speech.put("type", "PlainText");
		speech.put("text", response);

		Map<String, Object> card = new HashMap<String, Object>();
		card.put("type", "Standard");
		card.put("title", "MMTS Train");
		card.put("text", response);

		Map<String, Object> image = new HashMap<String, Object>();
		image.put("smallImageUrl", "https://home-automation.vadrin.com/images/icon.jpg");
		image.put("largeImageUrl", "https://home-automation.vadrin.com/images/icon.jpg");
		card.put("image", image);
		AlexaResponse toReturn = new AlexaResponse("1.0", new HashMap<String, Object>(),
				new AlexaCardAndSpeech(speech, card, endSession, null));
		System.out.println("respose is - " + JsonService.getJson(toReturn).toString());
		return toReturn;
	}

	private AlexaResponse autoFetchSlots(JsonNode alexaRequestBody) {
		JsonNode toClearSlots = alexaRequestBody.get("request").get("intent");
		boolean cleared = false;
		try {
			if (toClearSlots.get("slots").get("from").get("resolutions").get("resolutionsPerAuthority").get(0).get("status")
					.get("code").asText().endsWith("NO_MATCH")) {
				((ObjectNode) toClearSlots.get("slots").get("from")).remove("value");
				((ObjectNode) toClearSlots.get("slots").get("from")).remove("resolutions");
				((ObjectNode) toClearSlots.get("slots").get("from")).remove("source");
				cleared = true;
			}
		}catch(NullPointerException e) {
			
		}
		try {
			if (toClearSlots.get("slots").get("to").get("resolutions").get("resolutionsPerAuthority").get(0).get("status")
					.get("code").asText().endsWith("NO_MATCH")) {
				((ObjectNode) toClearSlots.get("slots").get("to")).remove("value");
				((ObjectNode) toClearSlots.get("slots").get("to")).remove("resolutions");
				((ObjectNode) toClearSlots.get("slots").get("to")).remove("source");
				cleared = true;
			}
		}catch(NullPointerException e) {
			
		}
		List<Map<String, Object>> directives = new ArrayList<Map<String, Object>>();
		Map<String, Object> autofetch = new HashMap<String, Object>();
		autofetch.put("type", "Dialog.Delegate");
		if (cleared)
			autofetch.put("updatedIntent", JsonService.getMapFromJson(toClearSlots));
		directives.add(autofetch);
		AlexaResponse toReturn = new AlexaResponse("1.0", new HashMap<String, Object>(),
				new AlexaCardAndSpeech(null, null, false, directives));
		System.out.println("respose is - " + JsonService.getJson(toReturn).toString());
		return toReturn;
	}

	private AlexaResponse constructAlexaResponse(Chat chat) {
		return constructAlexaResponse(chat.getMessage(), chat.isTheEnd());
	}
	

  private Chat handle(String conversationId, Event event) {
    System.out.println(conversationId);
    switch (event.getName()) {
    case "LaunchRequest":
      return new Chat(GREET, false);
    case "Default Welcome Intent":
      return new Chat(GREET, false);
    case "AMAZON.HelpIntent":
      return new Chat(GREET, false);
    case "WaterLevelIntent": {
      String device = intentsRepository.getDeviceName(event.getName());
      String reading = devicesRepository.getDeviceReading(device);
      if (reading == null || reading.isBlank())
        return new Chat(DONT_HAVE, true);
      else
        return new Chat(
            device + IS + devicesRepository.getDeviceReading(intentsRepository.getDeviceName(event.getName())), true);
    }
    case "AMAZON.CancelIntent":
      return new Chat(BYE, true);
    case "AMAZON.StopIntent":
      return new Chat(BYE, true);
    case "SessionEndedRequest":
      return new Chat(BYE, true);
    default:
      return new Chat(TROUBLE_UNDERSTANDING, true);
    }
  }

}
