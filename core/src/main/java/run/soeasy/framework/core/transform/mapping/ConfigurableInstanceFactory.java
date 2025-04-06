package run.soeasy.framework.core.transform.mapping;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class ConfigurableInstanceFactory extends InstanceFactoryRegistry {
	private final InstanceFactories factories = new InstanceFactories();

	public InstanceFactories getFactories() {
		return factories;
	}

	@Override
	public boolean canInstantiated(@NonNull TypeDescriptor requiredType) {
		return super.canInstantiated(requiredType) || factories.canInstantiated(requiredType);
	}

	@Override
	public Object newInstance(@NonNull TypeDescriptor requiredType) {
		if (super.canInstantiated(requiredType)) {
			return super.newInstance(requiredType);
		}
		return factories.newInstance(requiredType);
	}
}
