package io.basc.framework.core.convert.transform.stractegy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Accesstor;
import io.basc.framework.core.convert.transform.Mapper;
import io.basc.framework.core.convert.transform.Mapping;
import io.basc.framework.util.Registration;
import io.basc.framework.util.spi.ServiceMap;

public class ObjectMapper<K, V extends Accesstor, M extends Mapping<K, V>, E extends Throwable>
		extends DefaultMappingFactory<Object, K, V, M, E> implements Mapper<Object, Object, E> {
	private final ServiceMap<InstanceFactory> serviceMap = new ServiceMap<>();

	public Registration registerInstanceFactory(Class<?> requiredType, InstanceFactory instanceFactory) {
		return serviceMap.register(requiredType, instanceFactory);
	}

	@Override
	public boolean canInstantiated(TypeDescriptor type) {
		InstanceFactory instanceFactory = serviceMap.match(type.getType()).first();
		if (instanceFactory == null) {
			instanceFactory = DefaultInstanceFatory.getInstance();
		}
		return instanceFactory.canInstantiated(type);
	}

	@Override
	public Object newInstance(TypeDescriptor type) {
		InstanceFactory instanceFactory = serviceMap.match(type.getType()).first();
		if (instanceFactory == null) {
			instanceFactory = DefaultInstanceFatory.getInstance();
		}
		return instanceFactory.newInstance(type);
	}
}
