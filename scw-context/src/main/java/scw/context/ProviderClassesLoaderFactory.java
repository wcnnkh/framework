package scw.context;

public interface ProviderClassesLoaderFactory extends ClassesLoaderFactory {
	ClassesLoader getContextClassesLoader();
}
