package io.basc.framework.jackson;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.json.AbstractJsonElement;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonObject;

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

public class JacksonJsonElement extends AbstractJsonElement implements
		JsonSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	private final JsonNode jsonNode;
	private final ObjectMapper mapper;

	public JacksonJsonElement(JsonNode jsonNode,
			ObjectMapper mapper) {
		this.mapper = mapper;
		this.jsonNode = jsonNode;
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
	public String toJSONString() {
		return jsonNode.toString();
	}

	@Override
	protected Object getAsNonBaseType(TypeDescriptor type) {
		JavaType javaType = mapper.constructType(type.getResolvableType().getType());
		return mapper.convertValue(jsonNode, javaType);
	}

	@Override
	public void serialize(JsonGenerator gen, SerializerProvider serializers)
			throws IOException {
		jsonNode.serialize(gen, serializers);
	}

	@Override
	public void serializeWithType(JsonGenerator gen,
			SerializerProvider serializers, TypeSerializer typeSer)
			throws IOException {
		jsonNode.serializeWithType(gen, serializers, typeSer);
	}
}
