package run.soeasy.framework.core.execution.resolver;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.transform.stereotype.AccessDescriptor;
import run.soeasy.framework.util.spi.ConfigurableServices;

public class DefaultValueFactories extends ConfigurableServices<DefaultValueFactory> implements DefaultValueFactory {

	public DefaultValueFactories() {
		setServiceClass(DefaultValueFactory.class);
	}

	@Override
	public boolean hasDefaultValue(@NonNull AccessDescriptor descriptor) {
		return anyMatch((e) -> e.hasDefaultValue(descriptor));
	}

	@Override
	public Source getDefaultValue(@NonNull AccessDescriptor descriptor) {
		for (DefaultValueFactory resolver : this) {
			if (resolver.hasDefaultValue(descriptor)) {
				return resolver.getDefaultValue(descriptor);
			}
		}
		throw new UnsupportedOperationException(descriptor.toString());
	}
}
