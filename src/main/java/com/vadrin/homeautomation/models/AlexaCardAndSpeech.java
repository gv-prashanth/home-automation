package com.vadrin.homeautomation.models;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlexaCardAndSpeech {

	Map<String, Object> outputSpeech;
	Map<String, Object> card;
	List<Map<String, Object>> directives;
	boolean shouldEndSession;

	public Map<String, Object> getOutputSpeech() {
		return outputSpeech;
	}

	public void setOutputSpeech(Map<String, Object> outputSpeech) {
		this.outputSpeech = outputSpeech;
	}

	public Map<String, Object> getCard() {
		return card;
	}

	public void setCard(Map<String, Object> card) {
		this.card = card;
	}

	public boolean isShouldEndSession() {
		return shouldEndSession;
	}

	public void setShouldEndSession(boolean shouldEndSession) {
		this.shouldEndSession = shouldEndSession;
	}

	public AlexaCardAndSpeech() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<Map<String, Object>> getDirectives() {
		return directives;
	}

	public void setDirectives(List<Map<String, Object>> directives) {
		this.directives = directives;
	}

	public AlexaCardAndSpeech(Map<String, Object> outputSpeech, Map<String, Object> card, boolean shouldEndSession,
			List<Map<String, Object>> directives) {
		super();
		this.outputSpeech = outputSpeech;
		this.card = card;
		this.directives = directives;
		this.shouldEndSession = shouldEndSession;
	}

}
