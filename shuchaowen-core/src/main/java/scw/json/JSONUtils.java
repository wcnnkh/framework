package scw.json;

import java.lang.reflect.Type;

import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.util.FormatUtils;

public final class JSONUtils {
	private JSONUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	public static final JSONSupport DEFAULT_JSON_SUPPORT = InstanceUtils.getConfiguration(JSONSupport.class, InstanceUtils.REFLECTION_INSTANCE_FACTORY);

	static {
		if(DEFAULT_JSON_SUPPORT != null){
			FormatUtils.info(JSONUtils.class, "default json parse：{}", DEFAULT_JSON_SUPPORT.getClass().getName());
		}
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

	@SuppressWarnings("unchecked")
	public static <T> T parseObject(String text, Class<T> type) {
		if(type == JsonObject.class){
			return (T) parseObject(text);
		}else if(type == JsonArray.class){
			return (T) parseArray(text);
		}
		return DEFAULT_JSON_SUPPORT.parseObject(text, type);
	}

	@SuppressWarnings("unchecked")
	public static <T> T parseObject(String text, Type type) {
		if(type instanceof Class){
			return parseObject(text, (Class<T>)type);
		}
		
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
