package scw.json;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import scw.core.instance.InstanceUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
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

	private static Collection<String> getJsonParseSupportNames() {
		String value = SystemPropertyUtils.getProperty("scw.json.names");
		String[] arr = StringUtils.commonSplit(value);
		LinkedList<String> list = new LinkedList<String>();
		if (!ArrayUtils.isEmpty(arr)) {
			for (String name : arr) {
				list.add(name);
			}
		}

		list.add("scw.json.support.fastjson.FastJSONParseSupport");
		return list;
	}

	static {
		JSONParseSupport jsonSupport = null;
		for (String name : getJsonParseSupportNames()) {
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
		return ClassUtils.isExist("scw.json.support.fastjson.FastJSONParseSupport");
	}
}
