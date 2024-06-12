package io.basc.framework.mapper.transfer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Parameters;
import io.basc.framework.execution.Setter;
import io.basc.framework.mapper.InstanceFactory;
import io.basc.framework.mapper.stereotype.FieldDescriptor;
import io.basc.framework.mapper.stereotype.Mapping;
import io.basc.framework.mapper.stereotype.MappingFactory;
import io.basc.framework.mapper.transfer.convert.ParameterConverter;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Items;
import io.basc.framework.util.element.Elements;

public interface RecordConverter extends ReversibleConverter<Parameters, Object, ConversionException> {
	MappingFactory getMappingFactory();

	InstanceFactory getInstanceFactory();

	ConversionService getConversionService();

	ParameterConverter getParameterConverter();

	@SuppressWarnings("unchecked")
	@Override
	default Parameters reverseConvert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (source instanceof Parameters) {
			return (Parameters) source;
		} else if (getConversionService().canConvert(sourceType, Parameters.class)) {
			return getConversionService().convert(source, sourceType, Parameters.class);
		} else if (source instanceof Items) {
			Items<?> items = (Items<?>) source;
			return reverseConvertItems(items, sourceType);
		} else if (sourceType.isArray()) {
			return reverseConvertArray(source, sourceType.getElementTypeDescriptor());
		} else if (sourceType.isCollection()) {
			Collection<?> collection = (Collection<?>) source;
			return reverseConvertIterable(collection, sourceType.getElementTypeDescriptor());
		} else if (source instanceof Iterable) {
			Iterable<?> iterable = (Iterable<?>) source;
			return reverseConvertIterable(iterable, sourceType.getGeneric(0));
		} else if (source instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) source;
			return reverseConvertMap(map, sourceType.getMapKeyTypeDescriptor(), sourceType.getMapValueTypeDescriptor());
		} else if (getMappingFactory().isEntity(sourceType)) {
			Mapping<?> mapping = getMappingFactory().getMapping(sourceType.getType());
			return reverseConvertEntity(source, sourceType, mapping);
		} else {
			TypeDescriptor mapTypeDescriptor = TypeDescriptor.map(Map.class, String.class, Object.class);
			if (getConversionService().canConvert(sourceType, mapTypeDescriptor)) {
				Map<String, Object> map = (Map<String, Object>) getConversionService().convert(source, sourceType,
						mapTypeDescriptor);
				return reverseConvertMap(map, mapTypeDescriptor.getMapKeyTypeDescriptor(),
						mapTypeDescriptor.getMapValueTypeDescriptor());
			} else {
				Parameter parameter = getParameterConverter().reverseConvert(source, sourceType);
				return new Parameters(parameter);
			}
		}
	}

	default Parameters reverseConvertArray(Object array, TypeDescriptor elementTypeDescriptor) {
		Parameter[] parameters = new Parameter[Array.getLength(array)];
		for (int len = parameters.length, i = 0; i < len; i++) {
			Object value = Array.get(array, i);
			Parameter parameter = getParameterConverter().reverseConvert(value, elementTypeDescriptor);
			parameter.setPositionIndex(i);
			parameters[i] = parameter;
		}
		return new Parameters(parameters);
	}

	default Parameters reverseConvertEntity(Object entity, TypeDescriptor typeDescriptor, Mapping<?> mapping) {
		Elements<Parameter> elements = mapping.getElements().filter((e) -> e.isSupportGetter())
				.map((fieldDescriptor) -> {
					Object value = fieldDescriptor.getter().get(entity);
					Parameter parameter = new Parameter(fieldDescriptor.getPositionIndex(), fieldDescriptor.getName(),
							value, fieldDescriptor.getter().getTypeDescriptor());
					parameter.setAliasNames(fieldDescriptor.getAliasNames());
					return parameter;
				});
		return new Parameters(elements);
	}

	default Parameters reverseConvertItems(Items<?> items, TypeDescriptor elementTypeDescriptor) {
		Elements<Parameter> elements = items.getElements()
				.map((e) -> getParameterConverter().reverseConvert(e, elementTypeDescriptor));
		return new Parameters(elements);
	}

	default Parameters reverseConvertIterable(Iterable<?> iterable, TypeDescriptor elementTypeDescriptor) {
		Elements<?> elements = Elements.of(iterable);
		Elements<Parameter> parameters = elements.map((e) -> {
			return getParameterConverter().reverseConvert(e, elementTypeDescriptor);
		});
		return new Parameters(parameters);
	}

	default Parameters reverseConvertMap(Map<?, ?> map, TypeDescriptor keyTypeDescriptor,
			TypeDescriptor valueTypeDescriptor) {
		List<Parameter> list = new ArrayList<>();
		int index = 0;
		for (Entry<?, ?> entry : map.entrySet()) {
			Parameter parameter = getParameterConverter().reverseConvert(entry.getValue(), valueTypeDescriptor);
			parameter.setPositionIndex(index++);
			Object key = entry.getKey();
			if (key != null) {
				Value kValue = Value.of(key, keyTypeDescriptor);
				if (ClassUtils.isInt(keyTypeDescriptor.getType())) {
					parameter.setPositionIndex(kValue.getAsInt());
				} else {
					String name;
					if (getConversionService().canConvert(keyTypeDescriptor, String.class)) {
						name = getConversionService().convert(key, keyTypeDescriptor, String.class);
					} else {
						name = kValue.getAsString();
					}
					parameter.setName(name);
				}
			}
			list.add(parameter);
		}
		return new Parameters(list);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	default Object convert(Parameters source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (getConversionService().canConvert(sourceType, targetType)) {
			return getConversionService().convert(source, targetType);
		} else if (targetType.isArray()) {
			TypeDescriptor elementTypeDescriptor = targetType.getElementTypeDescriptor();
			Parameter[] parameters = source.getElements().toArray(Parameter[]::new);
			Object array = Array.newInstance(elementTypeDescriptor.getType(), parameters.length);
			for (int i = 0; i < parameters.length; i++) {
				Object value = getParameterConverter().convert(parameters[i], elementTypeDescriptor);
				Array.set(array, i, value);
			}
			return array;
		} else if (targetType.isCollection()) {
			TypeDescriptor elementTypeDescriptor = targetType.getElementTypeDescriptor();
			Collection<Object> collection = (Collection<Object>) getInstanceFactory().newInstance(targetType);
			for (Parameter parameter : source.getElements()) {
				Object value = getParameterConverter().convert(parameter, elementTypeDescriptor);
				collection.add(value);
			}
			return collection;
		} else if (targetType.isMap()) {
			TypeDescriptor keyTypeDescriptor = targetType.getMapKeyTypeDescriptor();
			TypeDescriptor valueTypeDescriptor = targetType.getMapValueTypeDescriptor();
			Map<Object, Object> map = (Map<Object, Object>) getInstanceFactory().newInstance(targetType);
			if (ClassUtils.isInt(keyTypeDescriptor.getType())) {
				int index = 0;
				for (Parameter parameter : source.getElements()) {
					Object key = index++;
					Object value = getParameterConverter().convert(parameter, valueTypeDescriptor);
					map.put(key, value);
				}
			} else {
				for (Parameter parameter : source.getElements()) {
					Object key = getConversionService().convert(parameter.getName(), keyTypeDescriptor);
					Object value = getParameterConverter().convert(parameter, valueTypeDescriptor);
					map.put(key, value);
				}
			}
			return map;
		} else if (getMappingFactory().isEntity(targetType)) {
			Mapping<?> mapping = getMappingFactory().getMapping(targetType.getType());
			Object entity = getInstanceFactory().newInstance(targetType);
			List<Parameter> parameters = new ArrayList<>(source.getElements().toList());
			for (FieldDescriptor fieldDescriptor : mapping.getElements()) {
				if (!fieldDescriptor.isSupportSetter()) {
					continue;
				}
				Setter setter = fieldDescriptor.setter();
				Iterator<Parameter> iterator = parameters.iterator();

				while (iterator.hasNext()) {
					Parameter parameter = iterator.next();
					if (parameter.getName().equals(fieldDescriptor.getName())
							|| parameter.getAliasNames().contains(fieldDescriptor.getName())
							|| fieldDescriptor.getAliasNames().contains(parameter.getName())
							|| fieldDescriptor.getAliasNames().anyMatch(parameter.getAliasNames(), String::equals)) {
						Object value = getParameterConverter().convert(parameter, setter.getTypeDescriptor());
						setter.set(entity, value);
						iterator.remove();
						break;
					}
				}
			}
			return entity;
		}
		throw new ConversionFailedException(sourceType, targetType, source, null);
	}
}
