package io.basc.framework.jackson;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.basc.framework.json.AbstractJson;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonElement;
import io.basc.framework.util.element.ConvertibleIterator;

public class JacksonJsonArray extends AbstractJson<Integer> implements JsonArray, JsonSerializable {
	private final ArrayNode arrayNode;
	private final ObjectMapper mapper;

	public JacksonJsonArray(ObjectMapper mapper, ArrayNode arrayNode) {
		this.mapper = mapper;
		this.arrayNode = arrayNode;
	}

	@Override
	public int size() {
		return arrayNode.size();
	}

	@Override
	public String toJsonString() {
		return arrayNode.toString();
	}

	public JsonElement convert(JsonNode o) {
		return new JacksonJsonElement(o, mapper);
	}

	@Override
	public Iterator<JsonElement> iterator() {
		return new ConvertibleIterator<JsonNode, JsonElement>(arrayNode.iterator(), this::convert);
	}

	@Override
	public JsonElement get(Integer index) {
		JsonNode jsonNode = arrayNode.get(index);
		return jsonNode == null ? JsonElement.EMPTY : convert(jsonNode);
	}

	@Override
	public boolean add(Object element) {
		JsonNode jsonNode = mapper.getNodeFactory().pojoNode(element);
		arrayNode.add(jsonNode);
		return true;
	}

	@Override
	public boolean remove(int index) {
		JsonNode jsonNode = arrayNode.remove(index);
		return jsonNode != null;
	}

	@Override
	public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
		arrayNode.serialize(gen, serializers);
	}

	@Override
	public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
			throws IOException {
		arrayNode.serializeWithType(gen, serializers, typeSer);
	}

}
