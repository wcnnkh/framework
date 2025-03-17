package run.soeasy.framework.beans.factory.config;

import run.soeasy.framework.beans.BeansException;
import run.soeasy.framework.util.spi.ConfigurableServices;

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
