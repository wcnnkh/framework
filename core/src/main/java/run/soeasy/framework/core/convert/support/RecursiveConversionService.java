package run.soeasy.framework.core.convert.support;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;
import run.soeasy.framework.core.collection.CollectionFactory;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;

/**
 * 递归转换
 */
@Data
public class RecursiveConversionService implements ConversionService {
	private final ConversionService objectConversionService;

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source instanceof Map && targetType.isMap()) {
			Map<Object, Object> sourceMap = (Map<Object, Object>) source;
			TypeDescriptor targetKeyTypeDescriptor = targetType.getMapKeyTypeDescriptor();
			Map<Object, Object> map = CollectionFactory.createMap(targetType.getType(),
					targetKeyTypeDescriptor == null ? null : targetKeyTypeDescriptor.getType(), sourceMap.size());
			TypeDescriptor sourceKeyTypeDescriptor = sourceType.getMapKeyTypeDescriptor();
			TypeDescriptor sourceValueTypeDescriptor = sourceType.getMapValueTypeDescriptor();
			TypeDescriptor targetValueTypeDescriptor = targetType.getMapValueTypeDescriptor();
			for (Entry<Object, Object> entry : sourceMap.entrySet()) {
				Object k = convert(entry.getKey(), sourceKeyTypeDescriptor, targetKeyTypeDescriptor);
				Object v = convert(entry.getValue(), sourceValueTypeDescriptor, targetValueTypeDescriptor);
				map.put(k, v);
			}
			return map;
		} else if (targetType.isCollection()) {
			if (sourceType.isArray()) {
				int length = Array.getLength(source);
				TypeDescriptor targetElementTypeDescriptor = targetType.getElementTypeDescriptor();
				Class<?> componentType = targetElementTypeDescriptor.getType();
				if (componentType == null) {
					componentType = Object.class;
				}

				TypeDescriptor sourceElementTypeDescriptor = sourceType.getElementTypeDescriptor();
				Object array = Array.newInstance(componentType, length);
				for (int i = 0; i < length; i++) {
					Object v = Array.get(source, i);
					v = convert(v, sourceElementTypeDescriptor, targetElementTypeDescriptor);
					Array.set(array, i, v);
				}
				return array;
			}

			Collection<Object> sourceCollection = (Collection<Object>) source;
			TypeDescriptor targetElementTypeDescriptor = targetType.getElementTypeDescriptor();
			Collection<Object> target = CollectionFactory.createCollection(targetType.getType(),
					targetElementTypeDescriptor == null ? null : targetElementTypeDescriptor.getType(),
					sourceCollection.size());
			TypeDescriptor sourceElementTypeDescriptor = sourceType.getElementTypeDescriptor();
			for (Object sourceValue : sourceCollection) {
				Object v = convert(sourceValue, sourceElementTypeDescriptor, targetElementTypeDescriptor);
				target.add(v);
			}
			return target;
		}
		return objectConversionService.convert(source, sourceType, targetType);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return sourceType.isAssignableTo(targetType);
	}

}
