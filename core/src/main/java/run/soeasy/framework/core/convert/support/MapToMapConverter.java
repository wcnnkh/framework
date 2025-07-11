package run.soeasy.framework.core.convert.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.convert.AbstractConditionalConverter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;
import run.soeasy.framework.core.domain.KeyValue;

class MapToMapConverter extends AbstractConditionalConverter {

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
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
		return getConverter().convert(sourceKey, sourceType.getMapKeyTypeDescriptor(sourceKey), targetType);
	}

	private Object convertValue(Object sourceValue, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (targetType == null) {
			return sourceValue;
		}
		return getConverter().convert(sourceValue, sourceType.getMapValueTypeDescriptor(sourceValue), targetType);
	}

	public Set<TypeMapping> getConvertibleTypeMappings() {
		return Collections.singleton(new TypeMapping(Map.class, Map.class));
	}

}
