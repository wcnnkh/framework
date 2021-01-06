package scw.convert.support;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;

public abstract class AbstractConversionService implements ConversionService {
	//default method
	
	@SuppressWarnings("unchecked")
	public <T> T convert(Object source, Class<T> targetType) {
		return (T) convert(source,
				source == null ? null : TypeDescriptor.forObject(source),
				TypeDescriptor.valueOf(targetType));
	}
}
