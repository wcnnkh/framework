package scw.json;

import java.lang.reflect.Type;

import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.json.support.MyGsonJSONParseSupport;
import scw.logger.LoggerUtils;

public final class JSONUtils {
	private JSONUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	public static final JSONParseSupport DEFAULT_JSON_SUPPORT;

	static {
		JSONParseSupport jsonSupport = null;
		String[] supportClass = SystemPropertyUtils.getArrayProperty(String.class, "json.support.class", new String[]{"scw.json.support.fastjson.FastJSONParseSupport"});
		for (String name : supportClass) {
			jsonSupport = InstanceUtils.getInstance(name);
			if (jsonSupport != null) {
				break;
			}
		}

		DEFAULT_JSON_SUPPORT = jsonSupport == null ? new MyGsonJSONParseSupport() : jsonSupport;
		LoggerUtils.info(JSONUtils.class, "default json parse：{}", DEFAULT_JSON_SUPPORT.getClass().getName());
	}

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

	public static <T> T parseObject(String text, Type type) {
		return DEFAULT_JSON_SUPPORT.parseObject(text, type);
	}

	public static JSONArray createJSONArray() {
		return DEFAULT_JSON_SUPPORT.createJSONArray();
	}

	public static JSONObject createJSONObject() {
		return DEFAULT_JSON_SUPPORT.createJSONObject();
	}

	/**
	 * 是否支持fastjson
	 * 
	 * @return
	 */
	public static boolean isSupportFastJSON() {
		return ClassUtils.isAvailable("scw.json.support.fastjson.FastJSONParseSupport");
	}
}
