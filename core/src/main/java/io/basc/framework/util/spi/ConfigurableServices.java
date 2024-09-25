package io.basc.framework.util.spi;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.actor.ChangeEvent;
import lombok.NonNull;

public class ConfigurableServices<S> extends InjectableServices<S> implements Configurable {
	protected volatile Registration configureRegistration;
	private volatile Class<S> serviceClass;

	public ConfigurableServices() {
		this(Publisher.empty());
	}

	public ConfigurableServices(@NonNull Publisher<? super Elements<ChangeEvent<S>>> publisher) {
		super(publisher);
	}

	@Override
	public Receipt doConfigure(ServiceLoaderDiscovery discovery) {
		synchronized (this) {
			if (serviceClass == null) {
				return Receipt.fail();
			}

			ServiceLoader<S> serviceLoader = discovery.getServiceLoader(serviceClass);
			if (serviceLoader == null) {
				return Receipt.fail();
			}

			if (configureRegistration != null) {
				configureRegistration.cancel();
			}

			configureRegistration = registers(serviceLoader);
			return Receipt.success();
		}
	}

	public Class<S> getServiceClass() {
		synchronized (this) {
			return serviceClass;
		}
	}

	public void setServiceClass(Class<S> serviceClass) {
		synchronized (this) {
			this.serviceClass = serviceClass;
		}
	}
}
