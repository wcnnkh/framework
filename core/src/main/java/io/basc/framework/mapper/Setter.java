package io.basc.framework.mapper;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;

public interface Setter extends FieldDescriptor {
	public static final String DEFAULT_SETTER_METHOD_PREFIX = "set";

	void set(Object instance, Object value);

	default void set(Object instance, Object value, ConversionService conversionService) {
		if (conversionService == null) {
			set(instance, value);
			return;
		}

		Object targetValue = conversionService.convert(value, TypeDescriptor.forObject(value),
				new TypeDescriptor(this));
		// 此处不对插入值做非空验证，因为允许插入空值
		set(instance, targetValue);
	}
}