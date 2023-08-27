package com.vadrin.homeautomation.services;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonService {

	private static ObjectMapper om = new ObjectMapper();

	public static <T> JsonNode getJsonFromMap(Map<String, T> jsonMap) throws IllegalArgumentException {
		return om.convertValue(jsonMap, JsonNode.class);
	}

	public static JsonNode getJsonFromString(String jsonString)
			throws IOException, JsonParseException, JsonMappingException {
		return om.readValue(jsonString, JsonNode.class);
	}

	public static JsonNode getJson(Object object) {
		return om.convertValue(object, JsonNode.class);
	}

	public static <T> Object getObjectFromJson(JsonNode jsonNode, Class<T> valueType) throws JsonProcessingException {
		return om.treeToValue(jsonNode, valueType);
	}

	public static <T> Map<String, T> getMapFromJson(JsonNode jsonNode) throws IllegalArgumentException {
		return om.convertValue(jsonNode, Map.class);
	}
	
	public static JsonNode getJson(byte[] bs) throws JsonParseException, JsonMappingException, IOException {
	  return om.readValue(bs, JsonNode.class);
	}

}
