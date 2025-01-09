package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;
import io.basc.framework.util.spi.ConfigurableServices;

public final class BeanFactoryPostProcessors extends ConfigurableServices<BeanFactoryPostProcessor>
		implements BeanFactoryPostProcessor {

	public BeanFactoryPostProcessors() {
		setServiceClass(BeanFactoryPostProcessor.class);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		for (BeanFactoryPostProcessor postProcessor : this) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

}
