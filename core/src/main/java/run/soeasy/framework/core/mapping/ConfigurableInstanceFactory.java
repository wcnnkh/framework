package run.soeasy.framework.core.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class ConfigurableInstanceFactory extends ConfigurableServices<InstanceFactory> implements InstanceFactory {

	public ConfigurableInstanceFactory() {
		setServiceClass(InstanceFactory.class);
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
