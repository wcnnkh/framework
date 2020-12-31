package scw.configure;

import scw.convert.TypeDescriptor;

public interface Configure{
	boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);
	
	void configuration(Object source, Object target);
	
	void configuration(Object source, Class<?> sourceType, Object target,
			Class<?> targetType);
	
	void configuration(Object source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType);
}
