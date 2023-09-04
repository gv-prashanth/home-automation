package com.vadrin.homeautomation.services;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonService {

	private ObjectMapper om = new ObjectMapper();

	public <T> JsonNode getJsonFromMap(Map<String, T> jsonMap) throws IllegalArgumentException {
		return om.convertValue(jsonMap, JsonNode.class);
	}

	public JsonNode getJsonFromString(String jsonString)
			throws IOException {
		return om.readValue(jsonString, JsonNode.class);
	}

	public JsonNode getJson(Object object) {
		return om.convertValue(object, JsonNode.class);
	}

	public <T> Object getObjectFromJson(JsonNode jsonNode, Class<T> valueType) throws JsonProcessingException {
		return om.treeToValue(jsonNode, valueType);
	}

	public <T> Map<String, T> getMapFromJson(JsonNode jsonNode) throws IllegalArgumentException {
		return om.convertValue(jsonNode, Map.class);
	}
	
	public JsonNode getJson(byte[] bs) throws IOException {
	  return om.readValue(bs, JsonNode.class);
	}

}
