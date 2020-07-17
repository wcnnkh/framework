package scw.json;

import java.lang.reflect.Type;

import scw.compatible.CompatibleUtils;
import scw.compatible.ServiceLoader;
import scw.core.instance.InstanceUtils;
import scw.json.support.BuiltinGsonSupport;
import scw.util.FormatUtils;

public final class JSONUtils {
	public static final BuiltinGsonSupport BUILTIN_GSON_SUPPORT = new BuiltinGsonSupport();
	
	private JSONUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	public static final JSONSupport JSON_SUPPORT;

	static {
		JSONSupport jsonSupport = null;
		ServiceLoader<JSONSupport> serviceLoader = CompatibleUtils.getSpi().load(JSONSupport.class);
		for(JSONSupport support : serviceLoader){
			jsonSupport = support;
			break;
		}
		
		if(jsonSupport == null){
			for(String name : new String[]{"scw.json.support.FastJsonSupport"}){
				if(InstanceUtils.INSTANCE_FACTORY.isInstance(name)){
					jsonSupport = InstanceUtils.INSTANCE_FACTORY.getInstance(name);
					break;
				}
			}
		}

		JSON_SUPPORT = jsonSupport == null? BUILTIN_GSON_SUPPORT:jsonSupport;
		FormatUtils.info(JSONUtils.class, "using json support：{}", JSON_SUPPORT.getClass().getName());
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
