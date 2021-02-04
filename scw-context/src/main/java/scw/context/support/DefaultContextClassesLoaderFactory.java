package scw.context.support;

import scw.context.ClassesLoader;
import scw.context.ConfigurableClassesLoader;
import scw.context.ContextClassesLoaderFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultContextClassesLoaderFactory extends
		DefaultClassesLoaderFactory implements ContextClassesLoaderFactory {
	private final DefaultClassesLoader<?> contextClassesLoader = new DefaultClassesLoader();

	public DefaultContextClassesLoaderFactory(boolean cache) {
		super(cache);
		contextClassesLoader.add((ClassesLoader)new SystemContextClassesLoader<Object>(this, this));
	}

	public ConfigurableClassesLoader<?> getContextClassesLoader() {
		return contextClassesLoader;
	}
}
