package io.basc.framework.context.primary;

import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.util.element.Elements;

public class PrimaryResolvers extends ConfigurableServices<PrimaryResolver> implements PrimaryResolver {
	private static volatile PrimaryResolvers defaults;
	
	public static PrimaryResolvers defaults() {
		if(defaults == null) {
			synchronized (PrimaryResolvers.class) {
				if(defaults == null) {
					defaults = new PrimaryResolvers();
					defaults.configure(SPI.global());
				}
			}
		}
		return defaults;
	}
	
	public PrimaryResolvers() {
		super(PrimaryResolver.class);
	}

	@Override
	public Elements<BeanFactoryPostProcessor> getBeanFactoryPostProcessors(Class<?> primaryClass) {
		return getServices().flatMap((e) -> e.getBeanFactoryPostProcessors(primaryClass));
	}

}
