package io.basc.framework.json;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import io.basc.framework.env.Sys;
import io.basc.framework.gson.GsonSupport;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public final class JSONUtils {
	private static Logger logger = LoggerFactory.getLogger(JSONUtils.class);
	private static ThreadLocal<JSONSupport> local = new NamedThreadLocal<JSONSupport>(JSONUtils.class.getSimpleName());

	private JSONUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	public static final JSONSupport JSON_SUPPORT;

	static {
		JSONSupport jsonSupport = Sys.env.getServiceLoader(JSONSupport.class).first();
		JSON_SUPPORT = jsonSupport == null ? GsonSupport.INSTANCE : jsonSupport;
		logger.info("default json support [{}]", JSON_SUPPORT);
	}

	public static JSONSupport getDefaultJsonSupport() {
		return JSON_SUPPORT;
	}

	public static JSONSupport getJsonSupport() {
		JSONSupport jsonSupport = local.get();
		return jsonSupport == null ? JSON_SUPPORT : jsonSupport;
	}

	public static boolean hasJsonSupport() {
		return local.get() != null;
	}

	public static JSONSupport setJsonSupport(JSONSupport jsonSupport) {
		JSONSupport old = local.get();
		if (jsonSupport == null) {
			logger.debug("remove json support {}", old);
			local.remove();
		} else {
			logger.debug("set json support {}", jsonSupport);
			local.set(jsonSupport);
		}
		return old;
	}

	public static String toJSONString(Object obj) throws JSONException {
		return getJsonSupport().toJSONString(obj);
	}

	public static JsonElement parseJson(String text) throws JSONException {
		return getJsonSupport().parseJson(text);
	}

	public static JsonArray parseArray(String text) throws JSONException {
		return getJsonSupport().parseArray(text);
	}

	public static JsonObject parseObject(String text) throws JSONException {
		return getJsonSupport().parseObject(text);
	}

	public static JsonElement parseJson(Object obj) throws JSONException {
		return getJsonSupport().parseJson(obj);
	}

	public static <T> T parseObject(String text, Class<T> type) throws JSONException {
		return getJsonSupport().parseObject(text, type);
	}

	public static <T> T parseObject(String text, Type type) throws JSONException {
		return getJsonSupport().parseObject(text, type);
	}

	public static JsonArray parseArray(Reader reader) throws IOException, JSONException {
		return getJsonSupport().parseArray(reader);
	}

	public static JsonObject parseObject(Reader reader) throws IOException, JSONException {
		return getJsonSupport().parseObject(reader);
	}

	public static JsonElement parseJson(Reader reader) throws IOException, JSONException {
		return getJsonSupport().parseJson(reader);
	}

	public static <T> T parseObject(Reader reader, Class<T> type) throws IOException, JSONException {
		return getJsonSupport().parseObject(reader, type);
	}

	public static <T> T parseObject(Reader reader, Type type) throws IOException, JSONException {
		return getJsonSupport().parseObject(reader, type);
	}
}
