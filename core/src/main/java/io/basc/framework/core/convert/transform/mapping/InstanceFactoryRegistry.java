package io.basc.framework.core.convert.transform.mapping;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.spi.ServiceMap;
import lombok.NonNull;

public class InstanceFactoryRegistry extends ServiceMap<InstanceFactory> implements InstanceFactory {

	@Override
	public boolean canInstantiated(@NonNull TypeDescriptor requiredType) {
		InstanceFactory instanceFactory = search(requiredType.getType()).first();
		return instanceFactory == null ? false : instanceFactory.canInstantiated(requiredType);
	}

	@Override
	public Object newInstance(@NonNull TypeDescriptor requiredType) {
		InstanceFactory instanceFactory = search(requiredType.getType()).first();
		if (instanceFactory == null || !instanceFactory.canInstantiated(requiredType)) {
			throw new UnsupportedOperationException(requiredType.toString());
		}
		return instanceFactory.newInstance(requiredType);
	}

}
