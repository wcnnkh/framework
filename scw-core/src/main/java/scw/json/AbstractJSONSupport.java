package scw.json;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import scw.io.IOUtils;

public abstract class AbstractJSONSupport implements JSONSupport {

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(String text, Class<T> type) {
		if (type == JsonObject.class) {
			return (T) parseObject(text);
		} else if (type == JsonArray.class) {
			return (T) parseArray(text);
		}
		return parseObjectInternal(text, (Class<T>) type);
	}

	protected abstract <T> T parseObjectInternal(String text, Class<T> type);

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(String text, Type type) {
		if (type instanceof Class) {
			return parseObject(text, (Class<T>) type);
		}

		if (type == JsonObject.class) {
			return (T) parseObject(text);
		} else if (type == JsonArray.class) {
			return (T) parseArray(text);
		}

		return parseObjectInternal(text, type);
	}

	protected abstract <T> T parseObjectInternal(String text, Type type);

	public JsonArray parseArray(Reader reader) throws IOException {
		return parseArray(new String(IOUtils.toCharArray(reader)));
	}

	public JsonObject parseObject(Reader reader) throws IOException {
		return parseObject(new String(IOUtils.toCharArray(reader)));
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(Reader reader, Class<T> type) throws IOException {
		if (type == JsonObject.class) {
			return (T) parseObject(reader);
		} else if (type == JsonArray.class) {
			return (T) parseArray(reader);
		}
		return parseObjectInternal(reader, type);
	}

	protected <T> T parseObjectInternal(Reader reader, Class<T> type) throws IOException {
		return parseObject(new String(IOUtils.toCharArray(reader)), type);
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(Reader reader, Type type) throws IOException {
		if (type instanceof Class) {
			return parseObject(reader, (Class<T>) type);
		}

		if (type == JsonObject.class) {
			return (T) parseObject(reader);
		} else if (type == JsonArray.class) {
			return (T) parseArray(reader);
		}
		return parseObjectInternal(reader, type);
	}

	protected <T> T parseObjectInternal(Reader reader, Type type) throws IOException {
		return parseObject(new String(IOUtils.toCharArray(reader)), type);
	}
}
