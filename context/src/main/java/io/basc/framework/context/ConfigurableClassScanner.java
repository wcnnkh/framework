package io.basc.framework.context;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ServiceLoader;

public class ConfigurableClassScanner extends ConfigurableServices<ClassScanner> implements ClassScanner {

	public ConfigurableClassScanner() {
		super(ClassScanner.class);
	}

	@Override
	public boolean canScan(String pattern) {
		for (ClassScanner scanner : this) {
			if (scanner.canScan(pattern)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ServiceLoader<Class<?>> scan(String pattern, @Nullable TypeFilter filter) {
		for (ClassScanner scanner : this) {
			if (scanner.canScan(pattern)) {
				return scanner.scan(pattern, filter);
			}
		}
		return ServiceLoader.empty();
	}

}
