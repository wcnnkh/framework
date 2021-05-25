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

	public void addConversionService(ConversionService conversionService) {
		if (conversionService instanceof ConversionServiceAware) {
			((ConversionServiceAware) conversionService).setConversionService(awareConversionService);
		}

		synchronized (conversionServices) {
			conversionServices.add(conversionService);
		}
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
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
		
		if(parentConversionService != null && parentConversionService.canConvert(sourceType, targetType)) {
			return true;
		}
		
		return canDirectlyConvert(sourceType, targetType);
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (NESTED.exists(service)) {
				continue;
			}

			NESTED.set(service);
			try {
				if (service.canConvert(sourceType, targetType)) {
					return service.convert(source, sourceType, targetType);
				}
			} finally {
				NESTED.remove(service);
			}
		}
		
		if(parentConversionService != null && parentConversionService.canConvert(sourceType, targetType)) {
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
