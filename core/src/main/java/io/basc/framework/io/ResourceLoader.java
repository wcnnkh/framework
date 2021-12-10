package io.basc.framework.io;

import io.basc.framework.util.ClassLoaderProvider;

public interface ResourceLoader extends ClassLoaderProvider {

	/** Pseudo URL prefix for loading from the class path: "classpath:" */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

	/**
	 * Return a Resource handle for the specified resource location.
	 * <p>
	 * The handle should always be a reusable resource descriptor, allowing for
	 * multiple {@link Resource#getInputStream()} calls.
	 * <p>
	 * <ul>
	 * <li>Must support fully qualified URLs, e.g. "file:C:/test.dat".
	 * <li>Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
	 * <li>Should support relative file paths, e.g. "WEB-INF/test.dat". (This will
	 * be implementation-specific, typically provided by an ApplicationContext
	 * implementation.)
	 * </ul>
	 * <p>
	 * Note that a Resource handle does not imply an existing resource; you need to
	 * invoke {@link Resource#exists} to check for existence.
	 * 
	 * @param location the resource location
	 * @return a corresponding Resource handle (never {@code null})
	 * @see #CLASSPATH_URL_PREFIX
	 * @see Resource#exists()
	 * @see Resource#getInputStream()
	 */
	Resource getResource(String location);

	/**
	 * Expose the ClassLoader used by this ResourceLoader.
	 * <p>
	 * Clients which need to access the ClassLoader directly can do so in a uniform
	 * manner with the ResourceLoader, rather than relying on the thread context
	 * ClassLoader.
	 * 
	 * @return the ClassLoader (only {@code null} if even the system ClassLoader
	 *         isn't accessible)
	 * @see io.basc.framework.util.ClassUtils#getDefaultClassLoader()
	 */
	ClassLoader getClassLoader();

}
