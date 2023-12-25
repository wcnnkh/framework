package io.basc.framework.io.scan;

import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.select.Selector;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ClassScanners extends Scanners<ClassScanner, AnnotationMetadata> implements ClassScanner {
	@NonNull
	private Selector<Elements<Class<?>>> mergedSelector = Selector.first();

	public ClassScanners() {
		setServiceClass(ClassScanner.class);
	}

	@Override
	public boolean canScanLocation(String location) {
		return getServices().anyMatch((e) -> e.canScanLocation(location));
	}

	@Override
	public boolean canScanPackageName(String packageName) {
		return getServices().anyMatch((e) -> e.canScanPackageName(packageName));
	}

	@Override
	public Elements<AnnotationMetadata> scanLocation(String location) {
		return getSelector()
				.apply(getServices().filter((e) -> e.canScanLocation(location)).map((e) -> e.scanLocation(location)));

	}

	@Override
	public Elements<AnnotationMetadata> scanPackageName(String packageName) {
		return getSelector().apply(getServices().filter((e) -> e.canScanPackageName(packageName))
				.map((e) -> e.scanPackageName(packageName)));

	}

	@Override
	public Elements<Class<?>> scan(String locationPattern, ClassLoader classLoader) {
		return getMergedSelector().apply(getServices().filter((e) -> e.canScan(locationPattern))
				.map((e) -> e.scan(locationPattern, classLoader)));
	}

	@Override
	public Elements<Class<?>> scanLocation(String location, ClassLoader classLoader) {
		return getMergedSelector().apply(getServices().filter((e) -> e.canScanLocation(location))
				.map((e) -> e.scanLocation(location, classLoader)));
	}

	@Override
	public Elements<Class<?>> scanPackageName(String packageName, ClassLoader classLoader) {
		return getMergedSelector().apply(getServices().filter((e) -> e.canScanPackageName(packageName))
				.map((e) -> e.scanPackageName(packageName, classLoader)));
	}
}
