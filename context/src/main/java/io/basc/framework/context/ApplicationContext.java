package io.basc.framework.context;

import io.basc.framework.env1.EnvironmentCapable;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.spi.ServiceLoader;

public interface ApplicationContext
		extends EnvironmentCapable, ClassLoaderProvider, ParentDiscover<ApplicationContext> {
	ServiceLoader<Class<?>> getSourceClasses();

	ServiceLoader<Class<?>> getContextClasses();

	ClassScanner getClassScanner();

	/**
	 * Return the parent context, or {@code null} if there is no parent and this is
	 * the root of the context hierarchy.
	 * 
	 * @return the parent context, or {@code null} if there is no parent
	 */
	@Nullable
	ApplicationContext getParent();
}