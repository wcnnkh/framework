package scw.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.instance.InstanceUtils;
import scw.json.parser.DefaultJSONSupport;
import scw.lang.NamedThreadLocal;

public final class JSONUtils {
	private static ThreadLocal<JSONSupport> local = new NamedThreadLocal<JSONSupport>(
			JSONUtils.class.getSimpleName());

	private JSONUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	public static final JSONSupport JSON_SUPPORT;

	static {
		JSONSupport jsonSupport = InstanceUtils.loadService(JSONSupport.class);
		JSON_SUPPORT = jsonSupport == null ? new DefaultJSONSupport()
				: jsonSupport;
	}

	public static JSONSupport getDefaultJsonSupport() {
		return JSON_SUPPORT;
	}

	public static JSONSupport getJsonSupport() {
		JSONSupport jsonSupport = local.get();
		return jsonSupport == null ? JSON_SUPPORT : jsonSupport;
	}
	
	public static boolean hasJsonSupport(){
		return local.get() != null;
	}
	
	public static JSONSupport setJsonSupport(JSONSupport jsonSupport){
		JSONSupport old = local.get();
		if(jsonSupport == null){
			local.remove();
		}else{
			local.set(jsonSupport);
		}
		return old;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> parseArray(JsonArray jsonArray, Type type) {
		if (jsonArray == null) {
			return null;
		}

		if (jsonArray.isEmpty()) {
			return Collections.emptyList();
		}

		List<T> list = new ArrayList<T>(jsonArray.size());
		for (int i = 0, len = jsonArray.size(); i < len; i++) {
			T value = (T) jsonArray.getObject(i, type);
			list.add(value);
		}
		return list;
	}

	public static <T extends JsonObjectWrapper> List<T> wrapper(
			JsonArray jsonArray, Class<? extends T> type) {
		if (jsonArray == null) {
			return null;
		}

		if (jsonArray.isEmpty()) {
			return Collections.emptyList();
		}

		List<T> list = new ArrayList<T>(jsonArray.size());
		for (int i = 0, len = jsonArray.size(); i < len; i++) {
			T value = InstanceUtils.INSTANCE_FACTORY.getInstance(type,
					jsonArray.getJsonObject(i));
			list.add(value);
		}
		return list;
	}
}
