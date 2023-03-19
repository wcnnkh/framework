package io.basc.framework.io;

import java.util.function.Supplier;

import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StaticSupplier;

public class PackageClassesLoader extends ClassPathClassesLoader {

	public PackageClassesLoader(ResourcePatternResolver resourcePatternResolver, String packageName) {
		this(resourcePatternResolver, new StaticSupplier<>(packageName));
	}

	public PackageClassesLoader(ResourcePatternResolver resourcePatternResolver, Supplier<String> packageSupplier) {
		super(resourcePatternResolver, () -> {
			String path = packageSupplier.get();
			if (path == null) {
				return null;
			}
			return ClassUtils.convertClassNameToResourcePath(path);
		});
	}

}
