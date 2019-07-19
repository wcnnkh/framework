package scw.json;

import scw.core.exception.NotSupportException;
import scw.core.reflect.ReflectUtils;
import scw.logger.LoggerUtils;

public final class JSONUtils {
	private JSONUtils() {
	};

	static {
		String[] supportClassNames = { "scw.json.support.fastjson.FastJSONParseSupport" };

		Class<?> jsonSupportClass = null;
		for (String name : supportClassNames) {
			try {
				jsonSupportClass = Class.forName(name);
				break;
			} catch (Throwable e) {
			}
		}

		if (jsonSupportClass == null) {
			throw new NotSupportException("not found default json parse support");
		}

		LoggerUtils.info(JSONUtils.class, "default json parse：{}", jsonSupportClass.getName());
		DEFAULT_JSON_SUPPORT = (JSONParseSupport) ReflectUtils.newInstance(jsonSupportClass);
	}

	/**
	 * 默认的json序列化工具
	 */
	public static final JSONParseSupport DEFAULT_JSON_SUPPORT;

	public static String toJSONString(Object obj) {
		return DEFAULT_JSON_SUPPORT.toJSONString(obj);
	}

	public static JSONObject parseObject(String text) {
		return DEFAULT_JSON_SUPPORT.parseObject(text);
	}

	public static JSONArray parseArray(String text) {
		return DEFAULT_JSON_SUPPORT.parseArray(text);
	}

	public static <T> T parseObject(String text, Class<T> type) {
		return DEFAULT_JSON_SUPPORT.parseObject(text, type);
	}

	public static JSONArray createJSONArray() {
		return DEFAULT_JSON_SUPPORT.createJSONArray();
	}

	public static JSONObject createJSONObject() {
		return DEFAULT_JSON_SUPPORT.createJSONObject();
	}
}
