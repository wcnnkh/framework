package io.basc.framework.io;

import java.util.function.Supplier;

import io.basc.framework.util.StaticSupplier;

/**
 * 扫描路径下所有class文件
 * 
 * @author wcnnkh
 *
 */
public class LocationClassesLoader extends LocationPatternClassesLoader {

	public static String cleanPath(String location) {
		return location.endsWith(SUFFIX) ? location
				: (location.endsWith("/") ? (location + "**/*" + SUFFIX) : (location + "/**/*" + SUFFIX));
	}

	public LocationClassesLoader(ResourcePatternResolver resourcePatternResolver, String location) {
		this(resourcePatternResolver, new StaticSupplier<>(location));
	}

	public LocationClassesLoader(ResourcePatternResolver resourcePatternResolver, Supplier<String> locationSupplier) {
		super(resourcePatternResolver, () -> {
			String location = locationSupplier.get();
			if (location == null) {
				return null;
			}

			return LocationClassesLoader.cleanPath(location);
		});
	}
}
