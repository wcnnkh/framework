package scw.convert.lang;

import java.util.TreeSet;

import scw.convert.ConfigurableConversionService;
import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.ConverterNotFoundException;
import scw.convert.TypeDescriptor;

public class ConversionServices extends ConvertibleConditionalComparator<Object>
		implements ConfigurableConversionService, Comparable<Object> {
	private final TreeSet<ConversionService> conversionServices = new TreeSet<ConversionService>(this);

	public void addConversionService(ConversionService conversionService) {
		if (conversionService instanceof ConversionServiceAware) {
			((ConversionServiceAware) conversionService).setConversionService(this);
		}

		synchronized (conversionServices) {
			conversionServices.add(conversionService);
		}
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (service.canConvert(sourceType, targetType)) {
				return true;
			}
		}
		return canDirectlyConvert(sourceType, targetType);
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (service.canConvert(sourceType, targetType)) {
				return service.convert(source, sourceType, targetType);
			}
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
