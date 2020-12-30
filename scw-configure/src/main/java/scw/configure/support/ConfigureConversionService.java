package scw.configure.support;

import java.util.Set;

import scw.configure.Configure;
import scw.convert.TypeDescriptor;
import scw.convert.support.ConditionalConversionService;
import scw.convert.support.ConvertiblePair;
import scw.core.instance.NoArgsInstanceFactory;

public class ConfigureConversionService extends
		ConditionalConversionService {
	private final Configure configuration;
	private final NoArgsInstanceFactory instanceFactory;
	private final Set<ConvertiblePair> convertibleTypes;

	public ConfigureConversionService(Configure configuration,
			NoArgsInstanceFactory instanceFactory,
			Set<ConvertiblePair> convertibleTypes) {
		this.configuration = configuration;
		this.instanceFactory = instanceFactory;
		this.convertibleTypes = convertibleTypes;
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

	@Override
	public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
		return super.canConvert(sourceType, targetType)
				&& instanceFactory.isInstance(targetType);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertibleTypes;
	}
}
