package scw.json;

import java.lang.reflect.Type;

import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.FormatUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.json.support.BuiltinGsonSupport;

public final class JSONUtils {
	private JSONUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	public static final JSONSupport DEFAULT_JSON_SUPPORT;

	static {
		JSONSupport jSONSupport = null;
		String[] supportClass = SystemPropertyUtils.getArrayProperty(String.class, "json.support.class",
				new String[] { "scw.json.support.FastJsonSupport" });
		for (String name : supportClass) {
			jSONSupport = InstanceUtils.getInstance(name);
			if (jSONSupport != null) {
				break;
			}
		}

		DEFAULT_JSON_SUPPORT = jSONSupport == null ? new BuiltinGsonSupport() : jSONSupport;
		FormatUtils.info(JSONUtils.class, "default json parse：{}", DEFAULT_JSON_SUPPORT.getClass().getName());
	}

	public static String toJSONString(Object obj) {
		return DEFAULT_JSON_SUPPORT.toJSONString(obj);
	}

	public static JsonObject parseObject(String text) {
		return DEFAULT_JSON_SUPPORT.parseObject(text);
	}

	public static JsonArray parseArray(String text) {
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
		return ClassUtils.isPresent("scw.json.support.FastJsonSupport");
	}
}
