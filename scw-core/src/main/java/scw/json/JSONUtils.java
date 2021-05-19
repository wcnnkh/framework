package scw.json;

import scw.gson.GsonSupport;
import scw.instance.InstanceUtils;
import scw.lang.NamedThreadLocal;

public final class JSONUtils {
	private static ThreadLocal<JSONSupport> local = new NamedThreadLocal<JSONSupport>(JSONUtils.class.getSimpleName());

	private JSONUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	public static final JSONSupport JSON_SUPPORT;

	static {
		JSONSupport jsonSupport = InstanceUtils.loadService(JSONSupport.class);
		JSON_SUPPORT = jsonSupport == null ? GsonSupport.INSTANCE : jsonSupport;
	}

	public static JSONSupport getDefaultJsonSupport() {
		return JSON_SUPPORT;
	}

	public static JSONSupport getJsonSupport() {
		JSONSupport jsonSupport = local.get();
		return jsonSupport == null ? JSON_SUPPORT : jsonSupport;
	}

	public static boolean hasJsonSupport() {
		return local.get() != null;
	}

	public static JSONSupport setJsonSupport(JSONSupport jsonSupport) {
		JSONSupport old = local.get();
		if (jsonSupport == null) {
			local.remove();
		} else {
			local.set(jsonSupport);
		}
		return old;
	}
}
