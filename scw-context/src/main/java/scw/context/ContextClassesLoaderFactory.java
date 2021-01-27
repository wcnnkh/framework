package scw.context;

public interface ContextClassesLoaderFactory extends ClassesLoaderFactory {
	ClassesLoader<?> getContextClassesLoader();
}
