package io.basc.framework.core.execution.resolver;

import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.AccessDescriptor;
import io.basc.framework.util.spi.ConfigurableServices;
import lombok.NonNull;

public class DefaultValueFactories extends ConfigurableServices<DefaultValueFactory> implements DefaultValueFactory {

	public DefaultValueFactories() {
		setServiceClass(DefaultValueFactory.class);
	}

	@Override
	public boolean hasDefaultValue(@NonNull AccessDescriptor descriptor) {
		return anyMatch((e) -> e.hasDefaultValue(descriptor));
	}

	@Override
	public Value getDefaultValue(@NonNull AccessDescriptor descriptor) {
		for (DefaultValueFactory resolver : this) {
			if (resolver.hasDefaultValue(descriptor)) {
				return resolver.getDefaultValue(descriptor);
			}
		}
		throw new UnsupportedOperationException(descriptor.toString());
	}
}
