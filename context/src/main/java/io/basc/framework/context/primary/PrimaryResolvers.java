package io.basc.framework.context.primary;

import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.spi.ConfigurableServices;

public class PrimaryResolvers extends ConfigurableServices<PrimaryResolver> implements PrimaryResolver {
	private static volatile PrimaryResolvers defaults;

	public static PrimaryResolvers defaults() {
		if (defaults == null) {
			synchronized (PrimaryResolvers.class) {
				if (defaults == null) {
					defaults = new PrimaryResolvers();
					defaults.doNativeConfigure();
				}
			}
		}
		return defaults;
	}

	public PrimaryResolvers() {
		setServiceClass(PrimaryResolver.class);
	}
	
	@Override
	public Elements<BeanFactoryPostProcessor> getBeanFactoryPostProcessors(Class<?> primaryClass) {
		return flatMap((e) -> e.getBeanFactoryPostProcessors(primaryClass));
	}

}
