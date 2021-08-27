package io.basc.framework.mapper;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;

public interface Setter extends FieldDescriptor {
	public static final String DEFAULT_SETTER_METHOD_PREFIX = "set";
	
	void set(Object instance, Object value);
	
	default void set(Object instance, Object value, ConversionService conversionService) {
		if(conversionService == null){
			set(instance, value);
			return ;
		}
		
		Object targetValue = conversionService.convert(value, TypeDescriptor.forObject(value), new TypeDescriptor(this));
		set(instance, targetValue);
	}
}