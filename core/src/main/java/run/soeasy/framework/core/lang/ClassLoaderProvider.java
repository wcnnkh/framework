package run.soeasy.framework.core.lang;

@FunctionalInterface
public interface ClassLoaderProvider {
	ClassLoader getClassLoader();
}
