package io.basc.framework.context.config;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.context.ClassScanner;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.element.ServiceLoader;

public class ConfigurableClassScanner extends ConfigurableServices<ClassScanner> implements ClassScanner {

	public ConfigurableClassScanner() {
		super(ClassScanner.class);
	}

	@Override
	public boolean canScan(String pattern) {
		for (ClassScanner scanner : this.getServices()) {
			if (scanner.canScan(pattern)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ServiceLoader<Class<?>> scan(String pattern, @Nullable ClassLoader classLoader,
			@Nullable TypeFilter filter) {
		for (ClassScanner scanner : this.getServices()) {
			if (scanner.canScan(pattern)) {
				return scanner.scan(pattern, classLoader, filter);
			}
		}
		return ServiceLoader.empty();
	}

}
