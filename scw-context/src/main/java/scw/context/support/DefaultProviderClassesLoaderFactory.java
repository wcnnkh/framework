package scw.context.support;

import scw.context.ConfigurableClassesLoader;
import scw.context.ProviderClassesLoaderFactory;
import scw.lang.Nullable;
import scw.util.ClassLoaderProvider;

public class DefaultProviderClassesLoaderFactory extends DefaultClassesLoaderFactory
		implements ProviderClassesLoaderFactory {
	private final DefaultClassesLoader contextClassesLoader = new DefaultClassesLoader();

	public DefaultProviderClassesLoaderFactory(boolean cache, @Nullable ClassLoaderProvider classLoaderProvider) {
		super(cache, classLoaderProvider);
		contextClassesLoader.add(new SystemContextClassesLoader(this, this));
	}

	public ConfigurableClassesLoader getContextClassesLoader() {
		return contextClassesLoader;
	}
}
