package io.basc.framework.json;

import io.basc.framework.env.Sys;
import io.basc.framework.gson.GsonSupport;
import io.basc.framework.lang.NamedInheritableThreadLocal;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;

public final class JsonUtils {
	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private JsonUtils() {
	};

	/**
	 * 默认的json序列化工具
	 */
	private static JsonSupport defaultSupport;

	public static JsonSupport getDefaultSupport() {
		return defaultSupport;
	}

	public static void setDefaultSupport(JsonSupport defaultSupport) {
		Assert.requiredArgument(defaultSupport != null, "defaultSupport");
		JsonUtils.defaultSupport = defaultSupport;
	}

	static {
		try {
			JsonSupport jsonSupport = Sys.getEnv().getServiceLoader(JsonSupport.class).first();
			defaultSupport = jsonSupport == null ? GsonSupport.INSTANCE : jsonSupport;
			logger.info("default json support [{}]", defaultSupport);
		} catch (Throwable e) {
			logger.error(e, "Initialize the default json support exception");
		}
	}

	private static final ThreadLocal<JsonSupport> LOCAL_SUPPORT = new NamedInheritableThreadLocal<JsonSupport>(
			JsonUtils.class.getSimpleName(), true) {
		protected JsonSupport initialValue() {
			return getDefaultSupport();
		};
	};

	public static ThreadLocal<JsonSupport> getLocalSupport() {
		return LOCAL_SUPPORT;
	}

	public static JsonSupport getSupport() {
		JsonSupport jsonSupport = LOCAL_SUPPORT.get();
		jsonSupport = jsonSupport == null ? defaultSupport : jsonSupport;
		if (jsonSupport == null) {
			throw new UnsupportedException(JsonSupport.class.getName());
		}
		return jsonSupport;
	}

	public static JsonSupport setSupport(JsonSupport jsonSupport) {
		JsonSupport old = LOCAL_SUPPORT.get();
		if (jsonSupport == null) {
			logger.debug("remove json support {}", old);
			LOCAL_SUPPORT.remove();
		} else {
			logger.debug("set json support {}", jsonSupport);
			LOCAL_SUPPORT.set(jsonSupport);
		}
		return old;
	}
}
