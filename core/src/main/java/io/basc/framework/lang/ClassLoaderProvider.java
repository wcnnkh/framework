package io.basc.framework.lang;

@FunctionalInterface
public interface ClassLoaderProvider {
	ClassLoader getClassLoader();
}
