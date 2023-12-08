package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;

public final class BeanFactoryPostProcessors extends ConfigurableServices<BeanFactoryPostProcessor>
		implements BeanFactoryPostProcessor {

	public BeanFactoryPostProcessors() {
		super(BeanFactoryPostProcessor.class);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		for (BeanFactoryPostProcessor postProcessor : getServices()) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

}
