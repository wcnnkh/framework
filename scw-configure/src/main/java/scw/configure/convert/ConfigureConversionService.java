package scw.configure.convert;

import scw.configure.Configure;
import scw.convert.TypeDescriptor;
import scw.convert.support.AbstractConversionService;
import scw.core.instance.NoArgsInstanceFactory;

public class ConfigureConversionService extends AbstractConversionService {
	private final Configure configuration;
	private final NoArgsInstanceFactory instanceFactory;

	public ConfigureConversionService(Configure configuration,
			NoArgsInstanceFactory instanceFactory) {
		this.configuration = configuration;
		this.instanceFactory = instanceFactory;
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if (source == null) {
			return null;
		}

		Object target = instanceFactory.getInstance(targetType.getType());
		configuration.configuration(source, sourceType, target, targetType);
		return target;
	}

	public boolean isSupported(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return configuration.isSupported(sourceType, targetType)
				&& instanceFactory.isInstance(targetType.getType());
	}
}
