package io.basc.framework.util;

import io.basc.framework.core.utils.ClassUtils;


public interface ClassLoaderProvider {
	/**
	 * @see ClassUtils#getDefaultClassLoader()
	 * @see ClassUtils#getClassLoader(ClassLoaderProvider)
	 * @return
	 */
	ClassLoader getClassLoader();
}
