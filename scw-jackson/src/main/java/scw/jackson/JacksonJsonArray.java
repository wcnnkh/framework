package scw.jackson;

import java.io.IOException;
import java.util.Iterator;

import scw.convert.Converter;
import scw.core.IteratorConverter;
import scw.json.AbstractJson;
import scw.json.EmptyJsonElement;
import scw.json.JsonArray;
import scw.json.JsonElement;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class JacksonJsonArray extends AbstractJson<Integer> implements
		JsonArray, JsonSerializable, Converter<JsonNode, JsonElement> {
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
	public String toJSONString() {
		return arrayNode.toString();
	}

	@Override
	public JsonElement convert(JsonNode o) {
		return new JacksonJsonElement(EmptyJsonElement.INSTANCE, o, mapper);
	}

	@Override
	public Iterator<JsonElement> iterator() {
		return new IteratorConverter<JsonNode, JsonElement>(
				arrayNode.iterator(), this);
	}

	@Override
	public JsonElement getValue(Integer index) {
		JsonNode jsonNode = arrayNode.get(index);
		return jsonNode == null ? null : convert(jsonNode);
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
	public void serialize(JsonGenerator gen, SerializerProvider serializers)
			throws IOException {
		arrayNode.serialize(gen, serializers);
	}

	@Override
	public void serializeWithType(JsonGenerator gen,
			SerializerProvider serializers, TypeSerializer typeSer)
			throws IOException {
		arrayNode.serializeWithType(gen, serializers, typeSer);
	}

}
