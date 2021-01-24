package scw.util;

import scw.core.utils.ClassUtils;


public interface ClassLoaderProvider {
	/**
	 * @see ClassUtils#getDefaultClassLoader()
	 * @see ClassUtils#getClassLoader(ClassLoaderProvider)
	 * @return
	 */
	ClassLoader getClassLoader();
}
