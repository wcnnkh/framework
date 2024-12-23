package io.basc.framework.core.convert.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.lang.AbstractConversionService;
import io.basc.framework.core.convert.service.ConditionalConversionService;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.service.ConvertiblePair;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.KeyValue;
import lombok.NonNull;

class MapToMapConversionService extends AbstractConversionService implements ConditionalConversionService {

	public MapToMapConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(@NonNull Value value, @NonNull TypeDescriptor targetType) throws ConversionException {
		Object source = value.get();
		if (source == null) {
			return null;
		}

		Map<Object, Object> sourceMap = (Map<Object, Object>) source;

		// Shortcut if possible...
		boolean copyRequired = !targetType.getType().isInstance(source);
		if (!copyRequired && sourceMap.isEmpty()) {
			return sourceMap;
		}
		TypeDescriptor keyDesc = targetType.getMapKeyTypeDescriptor();
		TypeDescriptor valueDesc = targetType.getMapValueTypeDescriptor();
		List<KeyValue<?, ?>> targetEntries = new ArrayList<>(sourceMap.size());
		TypeDescriptor sourceType = value.getTypeDescriptor();
		for (Map.Entry<Object, Object> entry : sourceMap.entrySet()) {
			Object sourceKey = entry.getKey();
			Object sourceValue = entry.getValue();
			Object targetKey = convertKey(sourceKey, sourceType, keyDesc);
			Object targetValue = convertValue(sourceValue, sourceType, valueDesc);
			targetEntries.add(KeyValue.of(targetKey, targetValue));
			if (sourceKey != targetKey || sourceValue != targetValue) {
				copyRequired = true;
			}
		}
		if (!copyRequired) {
			return sourceMap;
		}

		Map<Object, Object> targetMap = CollectionUtils.createMap(targetType.getType(),
				(keyDesc != null ? keyDesc.getType() : null), sourceMap.size());
		for (KeyValue<?, ?> entry : targetEntries) {
			targetMap.put(entry.getKey(), entry.getValue());
		}
		return targetMap;
	}

	private Object convertKey(Object sourceKey, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (targetType == null) {
			return sourceKey;
		}
		return getConversionService().convert(sourceKey, sourceType.getMapKeyTypeDescriptor(sourceKey), targetType);
	}

	private Object convertValue(Object sourceValue, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (targetType == null) {
			return sourceValue;
		}
		return getConversionService().convert(sourceValue, sourceType.getMapValueTypeDescriptor(sourceValue),
				targetType);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Map.class, Map.class));
	}

}
