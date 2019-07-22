package scw.core.instance;

import scw.core.exception.NotSupportException;
import scw.core.instance.support.ReflectionInstanceFactory;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.LoggerUtils;

public final class InstanceUtils {
	private InstanceUtils() {
	};

	private static final ReflectionInstanceFactory REFLECTION_INSTANCE_FACTORY = new ReflectionInstanceFactory();

	public static ReflectionInstanceFactory getReflectionInstanceFactory() {
		return REFLECTION_INSTANCE_FACTORY;
	}

	private static final NoArgsInstanceFactory NO_ARGS_INSTANCE_FACTORY;

	static {
		NoArgsInstanceFactory instanceFactory = REFLECTION_INSTANCE_FACTORY
				.getInstance("scw.core.instance.support.SunNoArgsInstanceFactory");
		if (instanceFactory == null) {
			instanceFactory = REFLECTION_INSTANCE_FACTORY
					.getInstance("scw.core.instance.support.UnsafeNoArgsInstanceFactory");
		}

		if (instanceFactory == null) {
			throw new NotSupportException("Instances that do not call constructors are not supported");
		}

		LoggerUtils.info(ReflectUtils.class, "default not call constructors instance factory：{}",
				instanceFactory.getClass().getName());
		try {
			NO_ARGS_INSTANCE_FACTORY = instanceFactory;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static NoArgsInstanceFactory getNotConstructorNoArgsInstanceFactory() {
		return NO_ARGS_INSTANCE_FACTORY;
	}
}
