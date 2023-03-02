package io.basc.framework.util;

@FunctionalInterface
public interface ClassLoaderProvider {
	ClassLoader getClassLoader();
}
