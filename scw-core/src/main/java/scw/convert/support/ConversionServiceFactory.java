package scw.convert.support;

import java.lang.reflect.Type;
import java.util.SortedSet;
import java.util.TreeSet;

import scw.convert.ConversionService;
import scw.convert.ConverterNotFoundException;
import scw.convert.TypeDescriptor;
import scw.util.XUtils;

public class ConversionServiceFactory extends ConvertibleConditionalComparator<Object> implements ConversionService, Comparable<Object> {
	protected final TreeSet<ConversionService> conversionServices = new TreeSet<ConversionService>(
			this);

	public ConversionService getConversionService(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (service.isSupported(sourceType, targetType)) {
				return service;
			}
		}
		return null;
	}

	public SortedSet<ConversionService> getConversionServices() {
		return XUtils.synchronizedProxy(conversionServices, this);
	}

	public boolean isSupported(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		for (ConversionService service : conversionServices) {
			if (service.isSupported(sourceType, targetType)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public final <T> T convert(Object source, Type type) {
		return (T) convert(source,
				source == null ? null : TypeDescriptor.forObject(source),
				TypeDescriptor.valueOf(type));
	}

	public final Object convert(Object source, TypeDescriptor targetType) {
		return convert(source,
				source == null ? null : TypeDescriptor.forObject(source),
				targetType);
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(sourceType != null && targetType != null && targetType.isAssignableTo(sourceType)){
			return source;
		}
		
		if(targetType.getType() == Object.class){
			return source;
		}
		
		for (ConversionService service : conversionServices) {
			if (service.isSupported(sourceType, targetType)) {
				return service.convert(source, sourceType, targetType);
			}
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
