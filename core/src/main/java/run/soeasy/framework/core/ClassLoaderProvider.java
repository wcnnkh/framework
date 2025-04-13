package run.soeasy.framework.core;

@FunctionalInterface
public interface ClassLoaderProvider {
	ClassLoader getClassLoader();
}
