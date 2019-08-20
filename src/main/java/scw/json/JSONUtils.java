package scw.json;

import java.lang.reflect.Type;

import scw.core.exception.NotSupportException;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.logger.LoggerUtils;

public final class JSONUtils {
	private JSONUtils() {
	};

	static {
		String[] supportClassNames = { "scw.json.support.fastjson.FastJSONParseSupport" };
		JSONParseSupport jsonSupport = null;
		for (String name : supportClassNames) {
			jsonSupport = InstanceUtils.getInstance(name);
			if (jsonSupport != null) {
				break;
			}
		}

		if (jsonSupport == null) {
			throw new NotSupportException("not found default json parse support");
		}

		LoggerUtils.info(JSONUtils.class, "default json parse：{}", jsonSupport.getClass().getName());
		DEFAULT_JSON_SUPPORT = jsonSupport;
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
	 * @return
	 */
	public static boolean isSupportFastJSON() {
		return ClassUtils.isExist("scw.json.support.fastjson.FastJSONParseSupport");
	}
}
