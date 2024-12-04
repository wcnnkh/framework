package io.basc.framework.core.convert;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.util.CollectionUtils;
import lombok.Data;

/**
 * 递归转换
 */
@Data
public class RecursiveConversionService implements ConversionService {
	private final ConversionService objectConversionService;

	protected boolean isUnconvertibleType(TypeDescriptor typeDescriptor) {
		return ValueWrapper.isUnconvertibleType(typeDescriptor.getType());
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source == null) {
			return source;
		}

		TypeDescriptor sourceTypeDescriptor = sourceType == null ? TypeDescriptor.forObject(source) : sourceType;
		TypeDescriptor targetTypeDescriptor = targetType == null ? TypeDescriptor.forObject(source) : targetType;
		if (isUnconvertibleType(targetTypeDescriptor)) {
			return source;
		}

		if (isUnconvertibleType(sourceTypeDescriptor.narrow(source))) {
			return source;
		}

		if (targetType.isMap()) {
			Map<Object, Object> sourceMap = (Map<Object, Object>) source;
			TypeDescriptor targetKeyTypeDescriptor = targetTypeDescriptor.getMapKeyTypeDescriptor();
			Map<Object, Object> map = CollectionUtils.createMap(targetTypeDescriptor.getType(),
					targetKeyTypeDescriptor == null ? null : targetKeyTypeDescriptor.getType(), sourceMap.size());
			TypeDescriptor sourceKeyTypeDescriptor = sourceTypeDescriptor.getMapKeyTypeDescriptor();
			TypeDescriptor sourceValueTypeDescriptor = sourceTypeDescriptor.getMapValueTypeDescriptor();
			TypeDescriptor targetValueTypeDescriptor = targetTypeDescriptor.getMapValueTypeDescriptor();
			for (Entry<Object, Object> entry : sourceMap.entrySet()) {
				Object key = convert(entry.getKey(), sourceKeyTypeDescriptor, targetKeyTypeDescriptor);
				Object value = convert(entry.getValue(), sourceValueTypeDescriptor, targetValueTypeDescriptor);
				map.put(key, value);
			}
			return map;
		} else if (targetType.isCollection()) {
			if (targetType.isArray()) {
				int length = Array.getLength(source);
				TypeDescriptor targetElementTypeDescriptor = targetTypeDescriptor.getElementTypeDescriptor();
				Class<?> componentType = targetElementTypeDescriptor.getType();
				if (componentType == null) {
					componentType = Object.class;
				}

				TypeDescriptor sourceElementTypeDescriptor = sourceTypeDescriptor.getElementTypeDescriptor();
				Object array = Array.newInstance(componentType, length);
				for (int i = 0; i < length; i++) {
					Object value = Array.get(source, i);
					value = convert(value, sourceElementTypeDescriptor, targetElementTypeDescriptor);
					Array.set(array, i, value);
				}
				return array;
			}

			Collection<Object> sourceCollection = (Collection<Object>) source;
			TypeDescriptor targetElementTypeDescriptor = targetTypeDescriptor.getElementTypeDescriptor();
			Collection<Object> target = CollectionUtils.createCollection(targetTypeDescriptor.getType(),
					targetElementTypeDescriptor == null ? null : targetElementTypeDescriptor.getType(),
					sourceCollection.size());
			TypeDescriptor sourceElementTypeDescriptor = sourceTypeDescriptor.getElementTypeDescriptor();
			for (Object sourceValue : sourceCollection) {
				Object value = convert(sourceValue, sourceElementTypeDescriptor, targetElementTypeDescriptor);
				target.add(value);
			}
			return target;
		}
		return objectConversionService.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return sourceType.isAssignableTo(targetType);
	}

}
