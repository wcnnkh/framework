package scw.json;

import java.lang.reflect.Type;

import scw.core.instance.InstanceUtils;
import scw.util.FormatUtils;

public final class JSONUtils {
	private JSONUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	public static final JSONSupport JSON_SUPPORT = InstanceUtils.getSystemConfiguration(JSONSupport.class);

	static {
		if (JSON_SUPPORT != null) {
			FormatUtils.info(JSONUtils.class, "default json parse：{}", JSON_SUPPORT.getClass().getName());
		}
	}

	public static JSONSupport getJsonSupport() {
		return JSON_SUPPORT;
	}

	public static String toJSONString(Object obj) {
		return JSON_SUPPORT.toJSONString(obj);
	}

	public static JsonObject parseObject(String text) {
		return JSON_SUPPORT.parseObject(text);
	}

	public static JsonArray parseArray(String text) {
		return JSON_SUPPORT.parseArray(text);
	}

	public static <T> T parseObject(String text, Class<T> type) {
		return JSON_SUPPORT.parseObject(text, type);
	}

	public static <T> T parseObject(String text, Type type) {
		return JSON_SUPPORT.parseObject(text, type);
	}
}
