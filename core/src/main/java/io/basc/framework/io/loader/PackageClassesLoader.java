package io.basc.framework.io.loader;

import java.util.function.Supplier;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.StaticSupplier;

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

	/**
	 * 扫描指定包下的类
	 * 
	 * @param packageName
	 * @param typeFilter
	 * @return
	 */
	public static Elements<Class<?>> scan(String packageName, @Nullable TypeFilter typeFilter) {
		Assert.requiredArgument(StringUtils.isNotEmpty(packageName), "packageName");
		PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
		PackageClassesLoader packageClassesLoader = new PackageClassesLoader(pathMatchingResourcePatternResolver,
				packageName);
		if (typeFilter != null) {
			packageClassesLoader.setTypeFilter(typeFilter);
		}
		return packageClassesLoader.getServices();
	}
}
