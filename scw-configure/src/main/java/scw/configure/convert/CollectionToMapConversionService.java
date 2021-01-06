package scw.configure.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.support.ConditionalConversionService;
import scw.convert.support.ConvertiblePair;
import scw.util.CollectionFactory;

public class CollectionToMapConversionService extends ConditionalConversionService{
	private final ConversionService conversionService;
	private final PrimaryKeyGetter primaryKeyGetter;
	
	public CollectionToMapConversionService(ConversionService conversionService, PrimaryKeyGetter primaryKeyGetter){
		this.conversionService = conversionService;
		this.primaryKeyGetter = primaryKeyGetter;
	}
	
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Collection.class, Map.class));
	}
	
	@Override
	public boolean isSupported(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(super.isSupported(sourceType, targetType)){
			TypeDescriptor descriptor = sourceType.getElementTypeDescriptor();
			if(descriptor.getType() == Object.class){
				return true;
			}
			return primaryKeyGetter.matches(descriptor);
		}
		
		return conversionService.isSupported(sourceType, TypeDescriptor.collection(List.class, Object.class));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(source == null){
			return null;
		}
		
		Collection sources;
		if(sourceType.isCollection()){
			sources = (Collection)source;
		}else{
			sources = (Collection) conversionService.convert(source, sourceType, TypeDescriptor.collection(List.class, Object.class));
		}

		Map map = CollectionFactory.createMap(targetType.getType(), targetType.getMapKeyTypeDescriptor().getType(), sources.size());
		for(Object item : sources){
			if(item == null){
				continue;
			}
			
			Object value = conversionService.convert(item, sourceType.elementTypeDescriptor(item), targetType.getMapValueTypeDescriptor());
			Object key = primaryKeyGetter.get(value, sourceType.narrow(value));
			key = conversionService.convert(key, sourceType.narrow(key), targetType.getMapKeyTypeDescriptor());
			map.put(key, value);
		}
		return map;
	}
}
