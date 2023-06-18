package io.basc.framework.context;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ServiceLoader;

/**
 * 类扫描器
 * 
 * @author wcnnkh
 *
 */
public interface ClassScanner {
	boolean canScan(String pattern);

	ServiceLoader<Class<?>> scan(String pattern, @Nullable ClassLoader classLoader, @Nullable TypeFilter filter);
}
