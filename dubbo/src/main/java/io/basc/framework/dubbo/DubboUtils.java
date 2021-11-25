package io.basc.framework.dubbo;

import java.lang.reflect.Method;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassUtils;

public final class DubboUtils {
	private static Logger logger = LoggerFactory.getLogger(DubboUtils.class);

	private DubboUtils() {
	};

	public static boolean isSupport() {
		return ClassUtils.isPresent("org.apache.dubbo.config.annotation.Service", null);
	}

	public static void registerDubboShutdownHook(ClassLoader classLoader) {
		Class<?> dubboShutdownHook = null;
		try {
			dubboShutdownHook = ClassUtils.forName("org.apache.dubbo.config.DubboShutdownHook", classLoader);
		} catch (ClassNotFoundException e1) {
		}

		if (dubboShutdownHook == null) {
			return;
		}

		try {
			Object obj = ReflectionUtils.invokeStaticMethod(dubboShutdownHook, "getDubboShutdownHook", new Class[0]);
			Method method = ReflectionUtils.findMethod(dubboShutdownHook, "register");
			method.invoke(obj);
		} catch (Exception e) {
			logger.error(e, "shutdown error");
		}
	}
}
