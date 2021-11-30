package io.basc.framework.util;

@FunctionalInterface
public interface ClassLoaderProvider {
	/**
	 * @see ClassUtils#getDefaultClassLoader()
	 * @see ClassUtils#getClassLoader(ClassLoaderProvider)
	 * @return
	 */
	ClassLoader getClassLoader();
}
