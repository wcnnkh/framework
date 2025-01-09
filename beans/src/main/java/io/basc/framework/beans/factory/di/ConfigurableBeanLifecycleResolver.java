package io.basc.framework.beans.factory.di;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.core.execution.Executable;
import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableBeanLifecycleResolver extends
		ConfigurableServices<BeanLifecycleResolver> implements BeanLifecycleResolver {

	public ConfigurableBeanLifecycleResolver() {
		setServiceClass(BeanLifecycleResolver.class);
	}

	@Override
	public boolean isStartupExecute(BeanFactory beanFactory, Executable executable) {
		return anyMatch((e) -> e.isStartupExecute(beanFactory, executable));
	}

	@Override
	public boolean isStoppedExecute(BeanFactory beanFactory, Executable executable) {
		return anyMatch((e) -> e.isStoppedExecute(beanFactory, executable));
	}
}
