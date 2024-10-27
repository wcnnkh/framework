package io.basc.framework.util.spi;

import java.util.concurrent.locks.Lock;

import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.ServiceLoader;

public class ConfigurableServices<S> extends Services<S> implements Configurable {
	private volatile Registration configureRegistration;
	private volatile Class<S> serviceClass;

	@Override
	public Receipt doConfigure(ServiceLoaderDiscovery discovery) {
		synchronized (this) {
			if (serviceClass == null) {
				return Receipt.FAILURE;
			}

			ServiceLoader<S> serviceLoader = discovery.getServiceLoader(serviceClass);
			if (serviceLoader == null) {
				return Receipt.FAILURE;
			}

			if (configureRegistration != null) {
				configureRegistration.cancel();
			}

			configureRegistration = registers(serviceLoader);
			return Receipt.SUCCESS;
		}
	}

	public Class<S> getServiceClass() {
		synchronized (this) {
			return serviceClass;
		}
	}

	public void setServiceClass(Class<S> serviceClass) {
		Lock lock = getReadWriteLock().writeLock();
		lock.lock();
		try {
			if (this.serviceClass == serviceClass) {
				return;
			}

			this.serviceClass = serviceClass;
			// 如果已经初始化了需要reload
		} finally {
			lock.unlock();
		}
	}
}
