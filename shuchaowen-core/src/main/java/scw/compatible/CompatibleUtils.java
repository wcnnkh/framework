package scw.compatible;

import java.nio.charset.Charset;

import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectionUtils;

public final class CompatibleUtils {
	private CompatibleUtils() {
	};

	private static final boolean isSupportJdk6;
	private static final StringOperations STRING_OPERATIONS = InstanceUtils
			.getConfiguration(StringOperations.class,
					InstanceUtils.REFLECTION_INSTANCE_FACTORY);

	static {
		isSupportJdk6 = ReflectionUtils.getMethod(String.class, "getBytes",
				new Class<?>[] { Charset.class }) != null;
	}

	public static boolean isSupportJdk6() {
		return isSupportJdk6;
	}

	public static StringOperations getStringOperations() {
		return STRING_OPERATIONS;
	}
}
