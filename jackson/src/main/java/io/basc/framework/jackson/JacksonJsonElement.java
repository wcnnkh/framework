package io.basc.framework.jackson;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.json.AbstractJsonElement;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonException;
import io.basc.framework.json.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class JacksonJsonElement extends AbstractJsonElement implements JsonSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	private final JsonNode jsonNode;
	private final JacksonConverter converter;

	@Override
	public Object getSource() {
		return jsonNode;
	}

	@Override
	public JsonArray getAsJsonArray() {
		return new JacksonJsonArray((ArrayNode) jsonNode, converter);
	}

	@Override
	public JsonObject getAsJsonObject() {
		return new JacksonJsonObject((ObjectNode) jsonNode, converter);
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

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws JsonException, ConversionException {
		return converter.convert(source, sourceType, targetType);
	}
}
