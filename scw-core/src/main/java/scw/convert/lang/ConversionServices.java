package scw.convert.lang;

import java.util.TreeSet;

import scw.convert.ConfigurableConversionService;
import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.ConverterNotFoundException;
import scw.convert.TypeDescriptor;
import scw.lang.LinkedThreadLocal;

public class ConversionServices extends ConvertibleConditionalComparator<Object>
		implements ConfigurableConversionService, Comparable<Object>, ConversionServiceAware {
	private static final LinkedThreadLocal<ConversionService> NESTED = new LinkedThreadLocal<ConversionService>(
			ConversionServices.class.getName());
	private final TreeSet<ConversionService> conversionServices = new TreeSet<ConversionService>(this);
	private ConversionService awareConversionService = this;
	private ConversionService parentConversionService;

	public ConversionServices() {
	}

	public ConversionServices(ConversionService parentConversionServices) {
		this.parentConversionService = parentConversionServices;
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.awareConversionService = conversionService;
	}

	protected void aware(ConversionService conversionService) {
		if (conversionService instanceof ConversionServiceAware) {
			((ConversionServiceAware) conversionService).setConversionService(awareConversionService);
		}
	}

	public void addConversionService(ConversionService conversionService) {
		aware(conversionService);
		synchronized (conversionServices) {
			conversionServices.add(conversionService);
		}
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (canConvert(service, sourceType, targetType)) {
				return true;
			}
		}

		if (parentConversionService != null && parentConversionService.canConvert(sourceType, targetType)) {
			return true;
		}

		return canDirectlyConvert(sourceType, targetType);
	}

	private boolean canConvert(ConversionService service, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (NESTED.exists(service)) {
			return false;
		}

		NESTED.set(service);
		try {
			return service.canConvert(sourceType, targetType);
		} finally {
			NESTED.remove(service);
		}
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (canConvert(service, sourceType, targetType)) {
				return service.convert(source, sourceType, targetType);
			}
		}

		if (parentConversionService != null && parentConversionService.canConvert(sourceType, targetType)) {
			return parentConversionService.convert(source, sourceType, targetType);
		}

		if (canDirectlyConvert(sourceType, targetType)) {
			return source;
		}

		throw new ConverterNotFoundException(sourceType, targetType);
	}

	public int compareTo(Object o) {
		for (ConversionService service : conversionServices) {
			if (ConvertibleConditionalComparator.INSTANCE.compare(service, o) == 1) {
				return 1;
			}
		}
		return -1;
	}
}
