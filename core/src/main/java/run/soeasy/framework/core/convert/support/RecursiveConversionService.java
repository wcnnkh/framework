package run.soeasy.framework.core.convert.support;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;

/**
 * 递归转换
 */
@Data
public class RecursiveConversionService implements ConversionService {
	private final ConversionService objectConversionService;

	@SuppressWarnings("unchecked")
	@Override
	public Object apply(@NonNull ValueAccessor value, @NonNull TypeDescriptor requiredTypeDescriptor)
			throws ConversionException {
		Object source = value.get();
		TypeDescriptor sourceTypeDescriptor = value.getTypeDescriptor();
		if (requiredTypeDescriptor.isMap()) {
			Map<Object, Object> sourceMap = (Map<Object, Object>) source;
			TypeDescriptor targetKeyTypeDescriptor = requiredTypeDescriptor.getMapKeyTypeDescriptor();
			Map<Object, Object> map = CollectionUtils.createMap(requiredTypeDescriptor.getType(),
					targetKeyTypeDescriptor == null ? null : targetKeyTypeDescriptor.getType(), sourceMap.size());
			TypeDescriptor sourceKeyTypeDescriptor = sourceTypeDescriptor.getMapKeyTypeDescriptor();
			TypeDescriptor sourceValueTypeDescriptor = sourceTypeDescriptor.getMapValueTypeDescriptor();
			TypeDescriptor targetValueTypeDescriptor = requiredTypeDescriptor.getMapValueTypeDescriptor();
			for (Entry<Object, Object> entry : sourceMap.entrySet()) {
				Object k = convert(entry.getKey(), sourceKeyTypeDescriptor, targetKeyTypeDescriptor);
				Object v = convert(entry.getValue(), sourceValueTypeDescriptor, targetValueTypeDescriptor);
				map.put(k, v);
			}
			return map;
		} else if (requiredTypeDescriptor.isCollection()) {
			if (requiredTypeDescriptor.isArray()) {
				int length = Array.getLength(source);
				TypeDescriptor targetElementTypeDescriptor = requiredTypeDescriptor.getElementTypeDescriptor();
				Class<?> componentType = targetElementTypeDescriptor.getType();
				if (componentType == null) {
					componentType = Object.class;
				}

				TypeDescriptor sourceElementTypeDescriptor = sourceTypeDescriptor.getElementTypeDescriptor();
				Object array = Array.newInstance(componentType, length);
				for (int i = 0; i < length; i++) {
					Object v = Array.get(source, i);
					v = convert(v, sourceElementTypeDescriptor, targetElementTypeDescriptor);
					Array.set(array, i, v);
				}
				return array;
			}

			Collection<Object> sourceCollection = (Collection<Object>) source;
			TypeDescriptor targetElementTypeDescriptor = requiredTypeDescriptor.getElementTypeDescriptor();
			Collection<Object> target = CollectionUtils.createCollection(requiredTypeDescriptor.getType(),
					targetElementTypeDescriptor == null ? null : targetElementTypeDescriptor.getType(),
					sourceCollection.size());
			TypeDescriptor sourceElementTypeDescriptor = sourceTypeDescriptor.getElementTypeDescriptor();
			for (Object sourceValue : sourceCollection) {
				Object v = convert(sourceValue, sourceElementTypeDescriptor, targetElementTypeDescriptor);
				target.add(v);
			}
			return target;
		}
		return objectConversionService.apply(value, requiredTypeDescriptor);
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return sourceType.isAssignableTo(targetType);
	}

}
