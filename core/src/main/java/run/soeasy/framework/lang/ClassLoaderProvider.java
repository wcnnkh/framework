package run.soeasy.framework.lang;

@FunctionalInterface
public interface ClassLoaderProvider {
	ClassLoader getClassLoader();
}
