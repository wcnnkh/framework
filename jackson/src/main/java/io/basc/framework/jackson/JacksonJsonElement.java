package io.basc.framework.jackson;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.json.AbstractJsonElement;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonObject;

public class JacksonJsonElement extends AbstractJsonElement implements JsonSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	private final JsonNode jsonNode;
	private final ObjectMapper mapper;

	public JacksonJsonElement(JsonNode jsonNode, ObjectMapper mapper) {
		this.mapper = mapper;
		this.jsonNode = jsonNode;
	}

	@Override
	public Object getSource() {
		return jsonNode;
	}

	@Override
	public JsonArray getAsJsonArray() {
		return new JacksonJsonArray(mapper, (ArrayNode) jsonNode);
	}

	@Override
	public JsonObject getAsJsonObject() {
		return new JacksonJsonObject(mapper, (ObjectNode) jsonNode);
	}

	@Override
	public boolean isJsonArray() {
		return jsonNode.isArray();
	}

	@Override
	public boolean isJsonObject() {
		return jsonNode.isContainerNode() && !jsonNode.isArray();
	}

	@Override
	public String getAsString() {
		return jsonNode.asText();
	}

	@Override
	public String toJsonString() {
		return jsonNode.toString();
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		JavaType javaType = mapper.constructType(targetType.getResolvableType().getType());
		return mapper.convertValue(source, javaType);
	}

	@Override
	public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
		jsonNode.serialize(gen, serializers);
	}

	@Override
	public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
			throws IOException {
		jsonNode.serializeWithType(gen, serializers, typeSer);
	}

	@Override
	public boolean isEmpty() {
		return jsonNode.isEmpty() || super.isEmpty();
	}
}
