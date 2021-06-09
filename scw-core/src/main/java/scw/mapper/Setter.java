package scw.mapper;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;

public interface Setter extends FieldDescriptor {
	void set(Object instance, Object value);
	
	default void set(Object instance, Object value, ConversionService conversionService) {
		Object targetValue = conversionService.convert(value, TypeDescriptor.forObject(value), new TypeDescriptor(this));
		set(instance, targetValue);
	}
}