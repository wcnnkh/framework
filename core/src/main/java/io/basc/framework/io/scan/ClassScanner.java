package io.basc.framework.io.scan;

import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.element.Elements;

/**
 * 类扫描
 * 
 * @author wcnnkh
 *
 */
public interface ClassScanner extends Scanner<AnnotationMetadata> {
	default Elements<Class<?>> scan(String locationPattern, @Nullable ClassLoader classLoader) {
		Elements<Class<?>> elements = scan(locationPattern).map((e) -> {
			return ClassUtils.getClass(e.getClassName(), classLoader);
		});
		return elements.filter((e) -> e != null);
	}

	default boolean canScanLocation(String location) {
		return canScan(cleanLocation(location));
	}

	public static String cleanLocation(String location) {
		return location.endsWith(ClassUtils.CLASS_FILE_SUFFIX) ? location
				: (location.endsWith("/") ? (location + "**/*" + ClassUtils.CLASS_FILE_SUFFIX)
						: (location + "/**/*" + ClassUtils.CLASS_FILE_SUFFIX));
	}

	default Elements<AnnotationMetadata> scanLocation(String location) {
		return scan(cleanLocation(location));
	}

	default Elements<Class<?>> scanLocation(String location, @Nullable ClassLoader classLoader) {
		Elements<Class<?>> elements = scanLocation(location).map((e) -> {
			return ClassUtils.getClass(e.getClassName(), classLoader);
		});
		return elements.filter((e) -> e != null);
	}

	default boolean canScanPackageName(String packageName) {
		return canScanLocation(ClassUtils.convertClassNameToResourcePath(packageName));
	}

	default Elements<AnnotationMetadata> scanPackageName(String packageName) {
		return scanLocation(ClassUtils.convertClassNameToResourcePath(packageName));
	}

	default Elements<Class<?>> scanPackageName(String packageName, @Nullable ClassLoader classLoader) {
		Elements<Class<?>> elements = scanPackageName(packageName).map((e) -> {
			return ClassUtils.getClass(e.getClassName(), classLoader);
		});
		return elements.filter((e) -> e != null);
	}
}
