package scw.core.instance;

import scw.core.exception.NotSupportException;
import scw.core.instance.support.ReflectionInstanceFactory;
import scw.core.logger.LoggerUtils;
import scw.core.reflect.ReflectUtils;

public final class InstanceUtils {
	private InstanceUtils() {
	};

	private static final NoArgsInstanceFactory NO_ARGS_INSTANCE_FACTORY;

	static {
		Class<?> clz = null;
		try {
			clz = Class.forName("scw.core.instance.support.SunNoArgsInstanceFactory");
		} catch (ClassNotFoundException e) {
		}

		if (clz == null) {
			throw new NotSupportException("Instances that do not call constructors are not supported");
		}

		LoggerUtils.info(ReflectUtils.class, "default not call constructors instance factory:{}", clz.getName());
		try {
			NO_ARGS_INSTANCE_FACTORY = (InstanceFactory) clz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static NoArgsInstanceFactory getNotConstructorNoArgsInstanceFactory() {
		return NO_ARGS_INSTANCE_FACTORY;
	}

	private static final ReflectionInstanceFactory REFLECTION_INSTANCE_FACTORY = new ReflectionInstanceFactory();

	public static ReflectionInstanceFactory getReflectionInstanceFactory() {
		return REFLECTION_INSTANCE_FACTORY;
	}
}
