package scw.convert.support;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;

public class ArrayToArrayConversionService extends ConditionalConversionService {
	private final ConversionService conversionService;

	public ArrayToArrayConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object[].class,
				Object[].class));
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(source == null){
			return null;
		}
		
		int len = Array.getLength(source);
		TypeDescriptor targetElementType = targetType.getElementTypeDescriptor();
		Object targetArray = Array.newInstance(targetElementType.getType(), len);
		for(int i=0; i<len; i++){
			Object sourceElement = Array.get(source, i);
			Object targetElement = conversionService.convert(sourceElement, sourceType.elementTypeDescriptor(sourceElement), targetElementType);
			Array.set(targetArray, i, targetElement);
		}
		return targetArray;
	}
}
