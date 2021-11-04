package io.basc.framework.convert.lang;

import java.util.TreeSet;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.lang.LinkedThreadLocal;
import io.basc.framework.value.EmptyValue;

public class ConversionServices extends ConfigurableServices<ConversionService>
		implements ConversionService, Comparable<Object>,
		Iterable<ConversionService> {
	private static final LinkedThreadLocal<ConversionService> NESTED = new LinkedThreadLocal<ConversionService>(
			ConversionServices.class.getName());

	public ConversionServices() {
		super(ConversionService.class, null, () -> new TreeSet<>(ConversionComparator.INSTANCE));
	}

	protected void aware(ConversionService conversionService) {
		if (conversionService instanceof ConversionServiceAware) {
			((ConversionServiceAware) conversionService).setConversionService(this);
		}
		super.aware(conversionService);
	}

	public final boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : this) {
			if (NESTED.exists(service)) {
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

		for (ConversionService service : this) {
			if (NESTED.exists(service)) {
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
		
		if(sourceTypeToUse == null) {
			Object value = EmptyValue.INSTANCE.getAsObject(targetType);
			return value;
		}

		throw new ConverterNotFoundException(sourceTypeToUse, targetType);
	}

	public int compareTo(Object o) {
		for (ConversionService service : this) {
			if (ConversionComparator.INSTANCE.compare(service, o) == -1) {
				return -1;
			}
		}
		return 1;
	}
}
