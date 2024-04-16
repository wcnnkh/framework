package io.basc.framework.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.basc.framework.json.AbstractJsonSupport;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonException;
import io.basc.framework.json.JsonSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JacksonJSONSupport extends AbstractJsonSupport {
	public static final JsonSupport INSTANCE = new JacksonJSONSupport();
	@NonNull
	private final JacksonConverter converter;

	public JacksonJSONSupport() {
		this(new JacksonConverter());
	}

	@Override
	public JsonElement parseJson(String text) {
		JsonNode jsonNode;
		try {
			jsonNode = converter.getMapper().readTree(text);
		} catch (JsonMappingException e) {
			throw new JsonException(e);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
		return new JacksonJsonElement(jsonNode, converter);
	}

	@Override
	protected String toJsonStringInternal(Object obj) {
		try {
			return converter.getMapper().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new JsonException(e);
		}
	}

}
