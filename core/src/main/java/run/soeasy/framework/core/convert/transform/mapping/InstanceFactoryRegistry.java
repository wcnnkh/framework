package run.soeasy.framework.core.convert.transform.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.spi.ServiceMap;

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
