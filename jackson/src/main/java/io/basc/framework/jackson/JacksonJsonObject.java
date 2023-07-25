package io.basc.framework.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.basc.framework.json.AbstractJson;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonObject;
import io.basc.framework.util.element.Elements;

public class JacksonJsonObject extends AbstractJson<String> implements JsonObject, JsonSerializable {
	private final ObjectNode objectNode;
	private final ObjectMapper mapper;

	public JacksonJsonObject(ObjectMapper mapper, ObjectNode objectNode) {
		this.mapper = mapper;
		this.objectNode = objectNode;
	}

	@Override
	public int size() {
		return objectNode.size();
	}

	@Override
	public JsonElement get(String key) {
		JsonNode jsonNode = objectNode.get(key);
		return jsonNode == null ? JsonElement.EMPTY : convert(jsonNode);
	}

	@Override
	public String toJsonString() {
		return objectNode.toString();
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(() -> objectNode.fieldNames());
	}

	@Override
	public boolean remove(String key) {
		return objectNode.remove(key) != null;
	}

	@Override
	public boolean put(String key, Object value) {
		objectNode.putPOJO(key, value);
		return false;
	}

	@Override
	public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
		objectNode.serialize(gen, serializers);
	}

	@Override
	public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
			throws IOException {
		objectNode.serializeWithType(gen, serializers, typeSer);
	}

	public JsonElement convert(JsonNode o) {
		return new JacksonJsonElement(o, mapper);
	}

}
