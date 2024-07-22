package io.basc.framework.json;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.IOUtils;

public interface JsonSupport extends JsonConverter {
	String toJsonString(Object obj) throws JsonException;

	JsonElement parseJson(String text) throws JsonException;

	default JsonArray parseArray(String text) throws JsonException {
		JsonElement jsonElement = parseJson(text);
		return jsonElement == null ? null : jsonElement.getAsJsonArray();
	}

	default JsonObject parseObject(String text) throws JsonException {
		JsonElement jsonElement = parseJson(text);
		return jsonElement == null ? null : jsonElement.getAsJsonObject();
	}

	default JsonElement parseJson(Object obj) throws JsonException {
		if (obj == null) {
			return null;
		}

		if (obj instanceof Reader) {
			try {
				return parseJson((Reader) obj);
			} catch (IOException e) {
				throw new JsonException(obj.toString(), e);
			}
		}

		if (obj instanceof String) {
			return parseJson((String) obj);
		}

		return parseJson(toJsonString(obj));
	}

	@SuppressWarnings("unchecked")
	default <T> T parseObject(String text, Class<T> type) throws JsonException {
		if (type == JsonObject.class) {
			return (T) parseObject(text);
		} else if (type == JsonArray.class) {
			return (T) parseArray(text);
		} else if (type == JsonElement.class) {
			return (T) parseJson(text);
		} else if (type == String.class) {
			return (T) text;
		}
		JsonElement jsonElement = parseJson(text);
		return jsonElement == null ? null : jsonElement.getAsObject(type);
	}

	@SuppressWarnings("unchecked")
	default <T> T parseObject(String text, Type type) throws JsonException {
		if (type instanceof Class) {
			return (T) parseObject(text, (Class<?>) type);
		}

		JsonElement jsonElement = parseJson(text);
		if (jsonElement == null) {
			return null;
		}
		return (T) jsonElement.getAsObject(type);
	}

	default JsonArray parseArray(Reader reader) throws IOException, JsonException {
		JsonElement jsonElement = parseJson(reader);
		return jsonElement == null ? null : jsonElement.getAsJsonArray();
	}

	default JsonObject parseObject(Reader reader) throws IOException, JsonException {
		JsonElement jsonElement = parseJson(reader);
		return jsonElement == null ? null : jsonElement.getAsJsonObject();
	}

	default JsonElement parseJson(Reader reader) throws IOException, JsonException {
		return parseJson(new String(IOUtils.toCharArray(reader)));
	}

	@SuppressWarnings("unchecked")
	default <T> T parseObject(Reader reader, Class<T> type) throws IOException, JsonException {
		if (type == JsonObject.class) {
			return (T) parseObject(reader);
		} else if (type == JsonArray.class) {
			return (T) parseArray(reader);
		} else if (type == JsonElement.class) {
			return (T) parseJson(reader);
		}
		return parseObject(new String(IOUtils.toCharArray(reader)), type);
	}

	@SuppressWarnings("unchecked")
	default <T> T parseObject(Reader reader, Type type) throws IOException, JsonException {
		if (type instanceof Class) {
			return (T) parseObject(reader, (Class<?>) type);
		}
		return parseObject(new String(IOUtils.toCharArray(reader)), type);
	}

	@Override
	default Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws JsonException {
		JsonElement jsonElement = parseJson(source);
		return jsonElement == null ? null : jsonElement.getAsObject(targetType);
	}
}
