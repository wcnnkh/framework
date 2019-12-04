package scw.json;

import java.lang.reflect.Type;

import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.FormatUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.json.support.GsonSupport;

public final class JSONUtils {
	private JSONUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	public static final JsonSupport DEFAULT_JSON_SUPPORT;

	static {
		JsonSupport jsonSupport = null;
		String[] supportClass = SystemPropertyUtils.getArrayProperty(String.class, "json.support.class",
				new String[] { "scw.json.support.fastjson.FastJsonSupport" });
		for (String name : supportClass) {
			jsonSupport = InstanceUtils.getInstance(name);
			if (jsonSupport != null) {
				break;
			}
		}

		DEFAULT_JSON_SUPPORT = jsonSupport == null ? new GsonSupport() : jsonSupport;
		FormatUtils.info(JSONUtils.class, "default json parse：{}", DEFAULT_JSON_SUPPORT.getClass().getName());
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

	/**
	 * 是否支持fastjson
	 * 
	 * @return
	 */
	public static boolean isSupportFastJSON() {
		return ClassUtils.isPresent("scw.json.support.fastjson.FastJSONParseSupport");
	}
}
