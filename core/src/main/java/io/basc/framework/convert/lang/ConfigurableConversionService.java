package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.lang.LinkedThreadLocal;
import io.basc.framework.util.Registration;
import io.basc.framework.value.Value;

public class ConfigurableConversionService extends ConfigurableServices<ConversionService>
		implements ConversionService, Comparable<Object> {
	private static final LinkedThreadLocal<ConversionService> NESTED = new LinkedThreadLocal<ConversionService>(
			ConfigurableConversionService.class.getName());

	public ConfigurableConversionService() {
		super(ConversionComparator.INSTANCE, ConversionService.class);
		getServiceInjectors().register((service) -> {
			if (service instanceof ConversionServiceAware) {
				((ConversionServiceAware) service).setConversionService(this);
			}
			return Registration.EMPTY;
		});
	}

	public final boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : getServices()) {
			if (NESTED.isCurrent(service)) {
				continue;
			}

			NESTED.set(service);
			try {
				if (service.canConvert(sourceType, targetType)) {
					return true;
				}
			} finally {
				NESTED.remove(service);
			}
		}
		return canDirectlyConvert(sourceType, targetType);
	}

	public final Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		TypeDescriptor sourceTypeToUse = sourceType;
		if (sourceType == null && source != null) {
			sourceTypeToUse = TypeDescriptor.forObject(source);
		}

		for (ConversionService service : getServices()) {
			if (NESTED.isCurrent(service)) {
				continue;
			}

			NESTED.set(service);
			try {
				if (service.canConvert(sourceType, targetType)) {
					return service.convert(source, sourceTypeToUse, targetType);
				}
			} finally {
				NESTED.remove(service);
			}
		}

		if (canDirectlyConvert(sourceTypeToUse, targetType)) {
			return source;
		}

		if (sourceTypeToUse == null) {
			Object value = Value.EMPTY.getAsObject(targetType);
			return value;
		}

		throw new ConverterNotFoundException(sourceTypeToUse, targetType);
	}

	public int compareTo(Object o) {
		for (ConversionService service : getServices()) {
			if (ConversionComparator.INSTANCE.compare(service, o) == -1) {
				return -1;
			}
		}
		return 1;
	}
}
