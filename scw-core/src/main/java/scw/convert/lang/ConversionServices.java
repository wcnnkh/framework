package scw.convert.lang;

import java.util.Iterator;
import java.util.TreeSet;

import scw.convert.ConfigurableConversionService;
import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.ConverterNotFoundException;
import scw.convert.TypeDescriptor;
import scw.lang.LinkedThreadLocal;

public class ConversionServices extends ConvertibleConditionalComparator<Object> implements
		ConfigurableConversionService, Comparable<Object>, ConversionServiceAware, Iterable<ConversionService> {
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

	@Override
	public Iterator<ConversionService> iterator() {
		return conversionServices.iterator();
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : this) {
			if(canConvert(sourceType, targetType, service)) {
				return true;
			}
		}

		if (parentConversionService != null && parentConversionService.canConvert(sourceType, targetType)) {
			return true;
		}

		return canDirectlyConvert(sourceType, targetType);
	}

	private boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType, ConversionService service) {
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
		TypeDescriptor sourceTypeToUse = sourceType;
		if (sourceType == null && source != null) {
			sourceTypeToUse = TypeDescriptor.forObject(source);
		}

		for (ConversionService service : this) {
			if(canConvert(sourceTypeToUse, targetType, service)) {
				return service.convert(source, sourceTypeToUse, targetType);
			}
		}

		if (parentConversionService != null && parentConversionService.canConvert(sourceTypeToUse, targetType)) {
			return parentConversionService.convert(source, sourceTypeToUse, targetType);
		}

		if (canDirectlyConvert(sourceTypeToUse, targetType)) {
			return source;
		}

		throw new ConverterNotFoundException(sourceTypeToUse, targetType);
	}

	public int compareTo(Object o) {
		for (ConversionService service : this) {
			if (ConvertibleConditionalComparator.INSTANCE.compare(service, o) == 1) {
				return 1;
			}
		}
		return -1;
	}
}
