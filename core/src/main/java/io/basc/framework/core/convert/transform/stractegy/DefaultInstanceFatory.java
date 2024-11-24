package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.spi.ConfigurableServices;

public class DefaultInstanceFatory extends ConfigurableServices<InstanceFactory> implements InstanceFactory {
	private static volatile DefaultInstanceFatory instance;

	public static DefaultInstanceFatory getInstance() {
		if (instance == null) {
			synchronized (DefaultInstanceFatory.class) {
				if (instance == null) {
					instance = new DefaultInstanceFatory();
				}
			}
		}
		return instance;
	}

	public DefaultInstanceFatory() {
		setServiceClass(InstanceFactory.class);
	}

	@Override
	public boolean canInstantiated(TypeDescriptor type) {
		return anyMatch((e) -> e.canInstantiated(type));
	}

	@Override
	public Object newInstance(TypeDescriptor type) {
		for (InstanceFactory instanceFactory : this) {
			if (instanceFactory.canInstantiated(type)) {
				return instanceFactory.newInstance(type);
			}
		}
		throw new UnsupportedOperationException(type.getName());
	}

}
