package io.basc.framework.data.transfer;

import java.awt.List;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.beans.BeanUtils;
import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 行类型数据转换器，一般可用于表格类数据转换为实体
 * 
 * @author wcnnkh
 *
 */
@Getter
@Setter
public class RowConverter implements Converter<Object, Object, ConversionException> {
	@NonNull
	private ObjectMapper objectMapper = BeanUtils.getMapper();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (objectMapper.canDirectlyConvert(sourceType, targetType)) {
			// 如果是可以直接转换的类型，直接返回
			return source;
		}

		if (sourceType.isArray()) {
			Object[] sourceArray = (Object[]) source;
			Collection sourceCollection = Arrays.asList(sourceArray);
			TypeDescriptor sourceTypeDescriptor = TypeDescriptor.collection(List.class,
					sourceType.getElementTypeDescriptor());
			return convert(sourceCollection, sourceTypeDescriptor, targetType);
		} else if (sourceType.isCollection()) {
			Collection sourceCollection = (Collection) source;
			return convert(sourceCollection, sourceType, targetType);
		} else if (sourceType.isMap()) {
			Map sourceMap = (Map) source;
			return convert(sourceMap, sourceType, targetType);
		} else {
			TypeDescriptor sourceTypeDescriptor = TypeDescriptor.map(LinkedHashMap.class, String.class, Object.class);
			Map<String, Object> sourceMap = (Map<String, Object>) objectMapper.convert(source, sourceType,
					sourceTypeDescriptor);
			return convert(sourceMap, sourceTypeDescriptor, targetType);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object convert(Collection source, TypeDescriptor sourceTypeDescriptor,
			TypeDescriptor targetTypeDescriptor) {
		if (targetTypeDescriptor.isArray()) {
			if (objectMapper.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
				return objectMapper.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
			}

			TypeDescriptor sourceElementTypeDescriptor = sourceTypeDescriptor.getElementTypeDescriptor();
			TypeDescriptor targetElementTypeDescriptor = targetTypeDescriptor.getElementTypeDescriptor();
			Object array = Array.newInstance(targetElementTypeDescriptor.getType(), source.size());
			Iterator<?> iterator = source.iterator();
			for (int i = 0; i < source.size() && iterator.hasNext(); i++) {
				Object element = iterator.next();
				element = objectMapper.convert(element, sourceElementTypeDescriptor, targetElementTypeDescriptor);
				Array.set(array, i, element);
			}
			return array;
		} else if (targetTypeDescriptor.isCollection()) {
			if (objectMapper.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
				return objectMapper.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
			}

			TypeDescriptor sourceElementTypeDescriptor = sourceTypeDescriptor.getElementTypeDescriptor();
			TypeDescriptor targetElementTypeDescriptor = targetTypeDescriptor.getElementTypeDescriptor();
			Collection<Object> target = (Collection<Object>) objectMapper.newInstance(targetTypeDescriptor);
			for (Object element : source) {
				Object targetElement = objectMapper.convert(element, sourceElementTypeDescriptor,
						targetElementTypeDescriptor);
				target.add(targetElement);
			}
			return target;
		} else {
			LinkedHashMap<Integer, Object> map = new LinkedHashMap<>(source.size());
			Iterator<?> iterator = source.iterator();
			for (int i = 0; i < source.size() && iterator.hasNext(); i++) {
				Object element = iterator.next();
				map.put(i, element);
			}
			TypeDescriptor mapTypeDescriptor = TypeDescriptor.map(LinkedHashMap.class,
					TypeDescriptor.valueOf(Integer.class), sourceTypeDescriptor.getElementTypeDescriptor());
			return convert(map, mapTypeDescriptor, targetTypeDescriptor);
		}
	}

	@SuppressWarnings("rawtypes")
	protected Object convert(Map source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor) {
		if (targetTypeDescriptor.isArray()) {
			Collection values = source.values();
			TypeDescriptor typeDescriptor = TypeDescriptor.collection(Collection.class,
					sourceTypeDescriptor.getMapValueTypeDescriptor());
			return objectMapper.convert(values, typeDescriptor, targetTypeDescriptor);
		} else if(targetTypeDescriptor.isMap()){
			
		}
		return null;
	}
}
