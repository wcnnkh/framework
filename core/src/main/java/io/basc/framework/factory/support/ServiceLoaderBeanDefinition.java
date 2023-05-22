package io.basc.framework.factory.support;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.util.ServiceLoader;

final class ServiceLoaderBeanDefinition extends FactoryBeanDefinition {

	public ServiceLoaderBeanDefinition(BeanFactory beanFactory, Class<?> type) {
		super(beanFactory, type);
		setExternal(true);
	}

	private volatile ServiceLoader<?> serviceLoader;

	public ServiceLoader<?> getServiceLoader() {
		if (serviceLoader == null) {
			synchronized (this) {
				if (serviceLoader == null) {
					serviceLoader = getBeanFactory().getServiceLoader(getTypeDescriptor().getType());
				}
			}
		}
		return serviceLoader;
	}

	@Override
	public boolean isInstance() {
		return getServiceLoader().iterator().hasNext();
	}

	@Override
	public Object create() throws InstanceException {
		return getServiceLoader().iterator().next();
	}
}
