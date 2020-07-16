package scw.compatible;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import scw.compatible.map.CompatibleMap;
import scw.compatible.map.Java5MapWrapper;
import scw.compatible.map.MapCompatible;
import scw.core.utils.ClassUtils;
import scw.util.JavaVersion;

public final class CompatibleUtils {
	private CompatibleUtils() {
	};

	private static final StringOperations STRING_OPERATIONS = (StringOperations) (JavaVersion.INSTANCE
			.getMasterVersion() >= 6 ? ClassUtils.createInstance("scw.compatible.Jdk6StringOperations")
			: new Jdk5CompatibleOperations());

	private static final SPI SPI = (scw.compatible.SPI) (JavaVersion.INSTANCE
			.getMasterVersion() >= 6 ? ClassUtils.createInstance("scw.compatible.DefaultSPI") : new InternalSPI());

	private static final MapCompatible MAP_COMPATIBLE = (MapCompatible) (JavaVersion.INSTANCE
			.getMasterVersion() >= 8 ? ClassUtils.createInstance("scw.compatible.map.Java8MapWrapper")
			: new Java5MapWrapper());

	public static StringOperations getStringOperations() {
		return STRING_OPERATIONS;
	}

	public static SPI getSpi() {
		return SPI;
	}

	public static MapCompatible getMapCompatible() {
		return MAP_COMPATIBLE;
	}

	public static <K, V> CompatibleMap<K, V> createMap(boolean concurrent) {
		Map<K, V> map = concurrent ? new ConcurrentHashMap<K, V>()
				: new HashMap<K, V>();
		return MAP_COMPATIBLE.wrapper(map);
	}
}
