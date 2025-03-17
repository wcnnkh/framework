package run.soeasy.framework.beans.factory.ioc;

import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.core.execution.Executable;
import run.soeasy.framework.util.spi.ConfigurableServices;

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
