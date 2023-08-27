package com.vadrin.homeautomation.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vadrin.homeautomation.models.Intent;
import com.vadrin.homeautomation.models.Response;
import com.vadrin.homeautomation.models.alexa.AlexaCardAndSpeech;
import com.vadrin.homeautomation.models.alexa.AlexaResponse;

@Service
public class AlexaService {

	@Autowired
	ChatService chatService;
	
	public AlexaResponse respond(JsonNode alexaRequestBody) {
    String conversationId = alexaRequestBody.get("session").get("sessionId").asText();
	  String requestType = alexaRequestBody.get("request").get("type").asText();

    //Arrive at intent name using intentrequest object. if not possible arrive using request type.
    String intentName = requestType.equalsIgnoreCase("IntentRequest") ? alexaRequestBody.get("request").get("intent").get("name").asText() : requestType;
    Map<String, String> intentParams = constructIntentParams(alexaRequestBody);
    Intent intent = new Intent(intentName, intentParams);
    Response response = chatService.handleIntentRequest(conversationId, intent);
    AlexaResponse toReturn =  constructAlexaResponse(response);
    return toReturn;
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

}
