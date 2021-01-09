package scw.configure.convert;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.support.ConditionalConversionService;
import scw.convert.support.ConvertiblePair;
import scw.util.CollectionFactory;

public class ArrayToMapConversionService extends ConditionalConversionService{
	private final ConversionService conversionService;
	private final PrimaryKeyGetter primaryKeyGetter;
	
	public ArrayToMapConversionService(ConversionService conversionService, PrimaryKeyGetter primaryKeyGetter){
		this.conversionService = conversionService;
		this.primaryKeyGetter = primaryKeyGetter;
	}
	
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object[].class, Map.class));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(source == null){
			return null;
		}

		int len = Array.getLength(source);
		Map map = CollectionFactory.createMap(targetType.getType(), targetType.getMapKeyTypeDescriptor().getType(), len);
		for(int i=0; i<len; i++){
			Object item = Array.get(source, i);
			if(item == null){
				continue;
			}
			
			TypeDescriptor elementType = sourceType.elementTypeDescriptor(item);
			Object key = primaryKeyGetter.get(item, elementType);
			key = conversionService.convert(key, elementType, targetType.getMapKeyTypeDescriptor());
			Object value = conversionService.convert(item, elementType, targetType.getMapValueTypeDescriptor());
			map.put(key, value);
		}
		return map;
	}
}
