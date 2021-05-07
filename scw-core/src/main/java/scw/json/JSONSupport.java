package scw.json;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import scw.io.IOUtils;

public interface JSONSupport {
	String toJSONString(Object obj) throws JSONException;

	JsonElement parseJson(String text) throws JSONException;

	default JsonArray parseArray(String text) throws JSONException {
		JsonElement jsonElement = parseJson(text);
		return jsonElement == null ? null : jsonElement.getAsJsonArray();
	}

	default JsonObject parseObject(String text) throws JSONException {
		JsonElement jsonElement = parseJson(text);
		return jsonElement == null ? null : jsonElement.getAsJsonObject();
	}

	default JsonElement parseJson(Object obj) throws JSONException {
		if(obj == null) {
			return null;
		}
		
		if (obj instanceof Reader) {
			try {
				return parseJson((Reader) obj);
			} catch (IOException e) {
				throw new JSONException(obj.toString(), e);
			}
		}
		
		if(obj instanceof String) {
			return parseJson((String)obj);
		}
		
		return parseJson(toJSONString(obj));
	}

	@SuppressWarnings("unchecked")
	default <T> T parseObject(String text, Class<T> type) throws JSONException {
		if (type == JsonObject.class) {
			return (T) parseObject(text);
		} else if (type == JsonArray.class) {
			return (T) parseArray(text);
		} else if (type.isAssignableFrom(JsonElement.class)) {
			return (T) parseJson(text);
		} else if (type == String.class) {
			return (T) text;
		}
		JsonElement jsonElement = parseJson(text);
		return jsonElement == null ? null : jsonElement.getAsObject(type);
	}

	@SuppressWarnings("unchecked")
	default <T> T parseObject(String text, Type type) throws JSONException {
		if (type instanceof Class) {
			return (T) parseObject(text, (Class<?>) type);
		}

		JsonElement jsonElement = parseJson(text);
		if (jsonElement == null) {
			return null;
		}
		return (T) jsonElement.getAsObject(type);
	}

	default JsonArray parseArray(Reader reader) throws IOException, JSONException {
		JsonElement jsonElement = parseJson(reader);
		return jsonElement == null ? null : jsonElement.getAsJsonArray();
	}

	default JsonObject parseObject(Reader reader) throws IOException, JSONException {
		JsonElement jsonElement = parseJson(reader);
		return jsonElement == null ? null : jsonElement.getAsJsonObject();
	}

	default JsonElement parseJson(Reader reader) throws IOException, JSONException {
		return parseJson(new String(IOUtils.toCharArray(reader)));
	}

	@SuppressWarnings("unchecked")
	default <T> T parseObject(Reader reader, Class<T> type) throws IOException, JSONException {
		if (type == JsonObject.class) {
			return (T) parseObject(reader);
		} else if (type == JsonArray.class) {
			return (T) parseArray(reader);
		} else if (type.isAssignableFrom(JsonElement.class)) {
			return (T) parseJson(reader);
		}
		return parseObject(new String(IOUtils.toCharArray(reader)), type);
	}

	@SuppressWarnings("unchecked")
	default <T> T parseObject(Reader reader, Type type) throws IOException, JSONException {
		if (type instanceof Class) {
			return (T) parseObject(reader, (Class<?>) type);
		}
		return parseObject(new String(IOUtils.toCharArray(reader)), type);
	}
}
