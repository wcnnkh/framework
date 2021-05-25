package scw.json;

import scw.env.Sys;
import scw.gson.GsonSupport;
import scw.lang.NamedThreadLocal;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public final class JSONUtils {
	private static Logger logger = LoggerFactory.getLogger(JSONUtils.class);
	private static ThreadLocal<JSONSupport> local = new NamedThreadLocal<JSONSupport>(JSONUtils.class.getSimpleName());

	private JSONUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	public static final JSONSupport JSON_SUPPORT;

	static {
		JSONSupport jsonSupport = Sys.getInstanceFactory().getServiceLoader(JSONSupport.class).getFirst();
		JSON_SUPPORT = jsonSupport == null ? GsonSupport.INSTANCE : jsonSupport;
		logger.info("default json support [{}]", JSON_SUPPORT);
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
			logger.debug("remove json support {}", old);
			local.remove();
		} else {
			logger.debug("set json support {}", jsonSupport);
			local.set(jsonSupport);
		}
		return old;
	}
}
