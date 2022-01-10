package io.basc.framework.dubbo;

import io.basc.framework.util.ClassUtils;

public final class DubboUtils {
	private DubboUtils() {
	};

	public static boolean isSupport() {
		return ClassUtils.isPresent("org.apache.dubbo.config.annotation.Service", null);
	}
}
