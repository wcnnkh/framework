package scw.convert.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.lang.Nullable;
import scw.util.CollectionFactory;
import scw.util.KeyValuePair;

public class MapToMapConversionService extends ConditionalConversionService{
	private final ConversionService conversionService;
	
	public MapToMapConversionService(ConversionService conversionService){
		this.conversionService = conversionService;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(source == null){
			return null;
		}
		
		Map<Object, Object> sourceMap = (Map) source;

		// Shortcut if possible...
		boolean copyRequired = !targetType.getType().isInstance(source);
		if (!copyRequired && sourceMap.isEmpty()) {
			return sourceMap;
		}
		TypeDescriptor keyDesc = targetType.getMapKeyTypeDescriptor();
		TypeDescriptor valueDesc = targetType.getMapValueTypeDescriptor();

		List<KeyValuePair> targetEntries = new ArrayList<KeyValuePair>(sourceMap.size());
		for (Map.Entry<Object, Object> entry : sourceMap.entrySet()) {
			Object sourceKey = entry.getKey();
			Object sourceValue = entry.getValue();
			Object targetKey = convertKey(sourceKey, sourceType, keyDesc);
			Object targetValue = convertValue(sourceValue, sourceType, valueDesc);
			targetEntries.add(new KeyValuePair(targetKey, targetValue));
			if (sourceKey != targetKey || sourceValue != targetValue) {
				copyRequired = true;
			}
		}
		if (!copyRequired) {
			return sourceMap;
		}

		Map<Object, Object> targetMap = CollectionFactory.createMap(targetType.getType(),
				(keyDesc != null ? keyDesc.getType() : null), sourceMap.size());
		for (KeyValuePair entry : targetEntries) {
			targetMap.put(entry.getKey(), entry.getValue());
		}
		return targetMap;
	}
	
	private Object convertKey(Object sourceKey, TypeDescriptor sourceType, @Nullable TypeDescriptor targetType) {
		if (targetType == null) {
			return sourceKey;
		}
		return this.conversionService.convert(sourceKey, sourceType.getMapKeyTypeDescriptor(sourceKey), targetType);
	}

	private Object convertValue(Object sourceValue, TypeDescriptor sourceType, @Nullable TypeDescriptor targetType) {
		if (targetType == null) {
			return sourceValue;
		}
		return this.conversionService.convert(sourceValue, sourceType.getMapValueTypeDescriptor(sourceValue), targetType);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Map.class, Map.class));
	}

}
