package scw.dubbo;

import java.lang.reflect.Method;

import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public final class DubboUtils {
	private static Logger logger = LoggerUtils.getLogger(DubboUtils.class);

	private DubboUtils() {
	};

	public static boolean isSupport() {
		return ClassUtils.isPresent("org.apache.dubbo.config.annotation.Service");
	}

	public static void registerDubboShutdownHook() {
		Class<?> dubboShutdownHook = null;
		try {
			dubboShutdownHook = ClassUtils.forName("org.apache.dubbo.config.DubboShutdownHook");
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
