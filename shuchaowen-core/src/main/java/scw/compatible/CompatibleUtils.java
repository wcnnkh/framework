package scw.compatible;

import java.nio.charset.Charset;

import scw.compatible.map.MapCompatible;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectionUtils;

public final class CompatibleUtils {
	private CompatibleUtils() {
	};

	private static final boolean isSupportJdk6;
	private static final StringOperations STRING_OPERATIONS = InstanceUtils
			.getSystemConfiguration(StringOperations.class);
	private static final SPI SPI = InstanceUtils
			.getSystemConfiguration(SPI.class);

	private static final MapCompatible MAP_COMPATIBLE = InstanceUtils
			.getSystemConfiguration(MapCompatible.class);

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

	public static SPI getSpi() {
		return SPI;
	}

	public static MapCompatible getMapCompatible() {
		return MAP_COMPATIBLE;
	}
}
