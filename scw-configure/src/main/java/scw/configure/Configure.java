package scw.configure;

import scw.convert.TypeDescriptor;

public interface Configure{
	boolean isSupported(TypeDescriptor sourceType, TypeDescriptor targetType);
	
	void configuration(Object source, TypeDescriptor sourceType, Object target,
			TypeDescriptor targetType);
}
