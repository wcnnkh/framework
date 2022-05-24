package io.basc.framework.jackson;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.basc.framework.convert.ConvertibleIterator;
import io.basc.framework.json.AbstractJson;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonObject;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Pair;

public class JacksonJsonObject extends AbstractJson<String>
		implements JsonObject, JsonSerializable{
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
	public JsonElement getValue(String key) {
		JsonNode jsonNode = objectNode.get(key);
		return jsonNode == null ? null : convert(jsonNode);
	}

	@Override
	public String toJSONString() {
		return objectNode.toString();
	}

	@Override
	public Iterator<Pair<String, JsonElement>> iterator() {
		return new ConvertibleIterator<Entry<String, JsonNode>, Pair<String, JsonElement>>(objectNode.fields(),
				(o) -> new Pair<String, JsonElement>(o.getKey(), JacksonJsonObject.this.convert(o.getValue())));
	}

	@Override
	public Set<String> keySet() {
		return Collections.list(CollectionUtils.toEnumeration(objectNode.fieldNames())).stream()
				.collect(Collectors.toSet());
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
