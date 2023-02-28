package io.basc.framework.json;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import io.basc.framework.env.Sys;
import io.basc.framework.gson.GsonSupport;
import io.basc.framework.lang.NamedInheritableThreadLocal;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public final class JsonUtils {
	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private JsonUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	public static final JsonSupport JSON_SUPPORT;

	static {
		JsonSupport jsonSupport = Sys.getEnv().getServiceLoader(JsonSupport.class).first();
		JSON_SUPPORT = jsonSupport == null ? GsonSupport.INSTANCE : jsonSupport;
		logger.info("default json support [{}]", JSON_SUPPORT);
	}

	public static JsonSupport getDefaultJsonSupport() {
		return JSON_SUPPORT;
	}

	private static ThreadLocal<JsonSupport> local = new NamedInheritableThreadLocal<JsonSupport>(
			JsonUtils.class.getSimpleName()) {
		protected JsonSupport initialValue() {
			return getDefaultJsonSupport();
		};
	};

	public static ThreadLocal<JsonSupport> getLocal() {
		return local;
	}

	public static JsonSupport getJsonSupport() {
		JsonSupport jsonSupport = local.get();
		return jsonSupport == null ? getDefaultJsonSupport() : jsonSupport;
	}

	public static boolean hasJsonSupport() {
		return local.get() != null;
	}

	public static JsonSupport setJsonSupport(JsonSupport jsonSupport) {
		JsonSupport old = local.get();
		if (jsonSupport == null) {
			logger.debug("remove json support {}", old);
			local.remove();
		} else {
			logger.debug("set json support {}", jsonSupport);
			local.set(jsonSupport);
		}
		return old;
	}

	public static String toJsonString(Object obj) throws JsonException {
		return getJsonSupport().toJsonString(obj);
	}

	public static JsonElement parseJson(String text) throws JsonException {
		return getJsonSupport().parseJson(text);
	}

	public static JsonArray parseArray(String text) throws JsonException {
		return getJsonSupport().parseArray(text);
	}

	public static JsonObject parseObject(String text) throws JsonException {
		return getJsonSupport().parseObject(text);
	}

	public static JsonElement parseJson(Object obj) throws JsonException {
		return getJsonSupport().parseJson(obj);
	}

	public static <T> T parseObject(String text, Class<T> type) throws JsonException {
		return getJsonSupport().parseObject(text, type);
	}

	public static <T> T parseObject(String text, Type type) throws JsonException {
		return getJsonSupport().parseObject(text, type);
	}

	public static JsonArray parseArray(Reader reader) throws IOException, JsonException {
		return getJsonSupport().parseArray(reader);
	}

	public static JsonObject parseObject(Reader reader) throws IOException, JsonException {
		return getJsonSupport().parseObject(reader);
	}

	public static JsonElement parseJson(Reader reader) throws IOException, JsonException {
		return getJsonSupport().parseJson(reader);
	}

	public static <T> T parseObject(Reader reader, Class<T> type) throws IOException, JsonException {
		return getJsonSupport().parseObject(reader, type);
	}

	public static <T> T parseObject(Reader reader, Type type) throws IOException, JsonException {
		return getJsonSupport().parseObject(reader, type);
	}
}
