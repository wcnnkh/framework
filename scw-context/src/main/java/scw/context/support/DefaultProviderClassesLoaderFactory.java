package scw.context.support;

import scw.context.ClassesLoader;
import scw.context.ConfigurableClassesLoader;
import scw.context.ProviderClassesLoaderFactory;
import scw.lang.Nullable;
import scw.util.ClassLoaderProvider;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultProviderClassesLoaderFactory extends DefaultClassesLoaderFactory
		implements ProviderClassesLoaderFactory {
	private final DefaultClassesLoader<?> contextClassesLoader = new DefaultClassesLoader();

	public DefaultProviderClassesLoaderFactory(boolean cache, @Nullable ClassLoaderProvider classLoaderProvider) {
		super(cache, classLoaderProvider);
		contextClassesLoader.add((ClassesLoader) new SystemContextClassesLoader<Object>(this, this));
	}

	public ConfigurableClassesLoader<?> getContextClassesLoader() {
		return contextClassesLoader;
	}
}
