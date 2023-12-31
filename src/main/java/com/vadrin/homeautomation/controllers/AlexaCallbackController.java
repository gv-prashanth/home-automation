package com.vadrin.homeautomation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.vadrin.homeautomation.models.alexa.AlexaResponse;
import com.vadrin.homeautomation.services.AlexaService;

@RestController
public class AlexaCallbackController {

	@Autowired
	AlexaService alexaService;

	@PostMapping("/callback/alexa")
	public AlexaResponse callback(@RequestBody JsonNode alexaRequestBody) {
		return alexaService.respond(alexaRequestBody);
	}

}