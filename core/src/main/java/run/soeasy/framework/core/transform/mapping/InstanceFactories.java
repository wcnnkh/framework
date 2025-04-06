package run.soeasy.framework.core.transform.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.spi.ConfigurableServices;

public class InstanceFactories extends ConfigurableServices<InstanceFactory> implements InstanceFactory {
	private static volatile InstanceFactories instance;

	public static InstanceFactories getInstance() {
		if (instance == null) {
			synchronized (InstanceFactories.class) {
				if (instance == null) {
					instance = new InstanceFactories();
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	@Override
	public boolean canInstantiated(@NonNull TypeDescriptor requiredType) {
		for (InstanceFactory instanceFactory : this) {
			if (instanceFactory.canInstantiated(requiredType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object newInstance(@NonNull TypeDescriptor requiredType) {
		for (InstanceFactory instanceFactory : this) {
			if (instanceFactory.canInstantiated(requiredType)) {
				return instanceFactory.newInstance(requiredType);
			}
		}
		throw new UnsupportedOperationException(requiredType.toString());
	}

}
