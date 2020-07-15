package scw.compatible;

import scw.compatible.map.Java5MapWrapper;
import scw.compatible.map.MapCompatible;
import scw.core.instance.InstanceUtils;
import scw.util.JavaVersion;

public final class CompatibleUtils {
	private CompatibleUtils() {
	};

	private static final StringOperations STRING_OPERATIONS = (StringOperations) (JavaVersion.INSTANCE
			.getMasterVersion() >= 6 ? InstanceUtils.INSTANCE_FACTORY.getInstance("scw.compatible.Jdk6StringOperations")
					: new Jdk5CompatibleOperations());

	private static final SPI SPI = (scw.compatible.SPI) (JavaVersion.INSTANCE.getMasterVersion() >= 6
			? InstanceUtils.INSTANCE_FACTORY.getInstance("scw.compatible.DefaultSPI") : new InternalSPI());

	private static final MapCompatible MAP_COMPATIBLE = (MapCompatible) (JavaVersion.INSTANCE.getMasterVersion() >= 8
			? InstanceUtils.INSTANCE_FACTORY.getInstance("scw.compatible.map.Java8MapWrapper") : new Java5MapWrapper());

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
