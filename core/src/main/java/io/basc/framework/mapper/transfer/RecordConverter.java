package io.basc.framework.mapper.transfer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Parameters;
import io.basc.framework.mapper.Item;
import io.basc.framework.mapper.Items;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.Named;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.Value;

public interface RecordConverter extends ReversibleConverter<Parameters, Object, ConversionException>{
	ObjectMapper getMapper();

	/**
	 * 将对象解析为参数
	 * 
	 * @param source
	 * @param sourceTypeDescriptor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	default Parameters parseObject(Object source, TypeDescriptor sourceTypeDescriptor) {
		if (source instanceof Parameters) {
			return (Parameters) source;
		} else if (getMapper().canConvert(sourceTypeDescriptor, Parameters.class)) {
			return getMapper().convert(source, sourceTypeDescriptor, Parameters.class);
		} else if (source instanceof Items) {
			Items<?> items = (Items<?>) source;
			return parseItems(items, sourceTypeDescriptor);
		} else if (sourceTypeDescriptor.isArray()) {
			return parseArray(source, sourceTypeDescriptor.getElementTypeDescriptor());
		} else if (sourceTypeDescriptor.isCollection()) {
			Collection<?> collection = (Collection<?>) source;
			return parseIterable(collection, sourceTypeDescriptor.getElementTypeDescriptor());
		} else if (source instanceof Iterable) {
			Iterable<?> iterable = (Iterable<?>) source;
			return parseIterable(iterable, sourceTypeDescriptor.getGeneric(0));
		} else if (source instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) source;
			return parseMap(map, sourceTypeDescriptor.getMapKeyTypeDescriptor(),
					sourceTypeDescriptor.getMapValueTypeDescriptor());
		} else if (getMapper().isEntity(sourceTypeDescriptor)) {
			Mapping<?> mapping = getMapper().getMapping(sourceTypeDescriptor.getType());
			return parseEntity(source, sourceTypeDescriptor, mapping);
		} else {
			TypeDescriptor mapTypeDescriptor = TypeDescriptor.map(Map.class, String.class, Object.class);
			if (getMapper().canConvert(sourceTypeDescriptor, mapTypeDescriptor)) {
				Map<String, Object> map = (Map<String, Object>) getMapper().convert(source, sourceTypeDescriptor,
						mapTypeDescriptor);
				return parseMap(map, mapTypeDescriptor.getMapKeyTypeDescriptor(),
						mapTypeDescriptor.getMapValueTypeDescriptor());
			} else {
				Parameter parameter = encapsulation(source, sourceTypeDescriptor);
				return new Parameters(parameter);
			}
		}
	}

	default Parameters parseArray(Object array, TypeDescriptor elementTypeDescriptor) throws IOException {
		Parameter[] parameters = new Parameter[Array.getLength(array)];
		for (int len = parameters.length, i = 0; i < len; i++) {
			Object value = Array.get(array, i);
			Parameter parameter = encapsulation(value, elementTypeDescriptor);
			parameter.setPositionIndex(i);
			parameters[i] = parameter;
		}
		return new Parameters(parameters);
	}

	default Parameters parseEntity(Object entity, TypeDescriptor typeDescriptor, Mapping<?> mapping) {
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

	default Parameters parseItems(Items<?> items, TypeDescriptor elementTypeDescriptor) {
		Elements<Parameter> elements = items.getElements().map((e) -> encapsulation(e, elementTypeDescriptor));
		return new Parameters(elements);
	}

	default Parameters parseIterable(Iterable<?> iterable, TypeDescriptor elementTypeDescriptor) {
		Elements<?> elements = Elements.of(iterable);
		Elements<Parameter> parameters = elements.map((e) -> {
			return encapsulation(e, elementTypeDescriptor);
		});
		return new Parameters(parameters);
	}

	default Parameters parseMap(Map<?, ?> map, TypeDescriptor keyTypeDescriptor, TypeDescriptor valueTypeDescriptor) {
		List<Parameter> list = new ArrayList<>();
		int index = 0;
		for (Entry<?, ?> entry : map.entrySet()) {
			Parameter parameter = encapsulation(entry.getValue(), valueTypeDescriptor);
			parameter.setPositionIndex(index++);
			Object key = entry.getKey();
			if (key != null) {
				Value kValue = Value.of(key, keyTypeDescriptor);
				if (ClassUtils.isInt(keyTypeDescriptor.getType())) {
					parameter.setPositionIndex(kValue.getAsInt());
				} else {
					String name;
					if (getMapper().canConvert(keyTypeDescriptor, String.class)) {
						name = getMapper().convert(key, keyTypeDescriptor, String.class);
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

	/**
	 * 打包成参数
	 * 
	 * @param source
	 * @param sourceTypeDescriptor
	 * @return
	 */
	default Parameter encapsulation(Object source, TypeDescriptor sourceTypeDescriptor) {
		if (source instanceof Parameter) {
			return (Parameter) source;
		}

		if (getMapper().canConvert(sourceTypeDescriptor, Parameter.class)) {
			return getMapper().convert(source, sourceTypeDescriptor, Parameter.class);
		}

		Value value;
		if (source instanceof Value) {
			value = (Value) source;
		} else if (getMapper().canConvert(sourceTypeDescriptor, Value.class)) {
			value = getMapper().convert(source, sourceTypeDescriptor, Value.class);
		} else {
			value = Value.of(source, sourceTypeDescriptor);
		}

		Parameter parameter;
		if (source instanceof Item) {
			Item item = (Item) source;
			parameter = new Parameter(item.getPositionIndex(), item.getName(), value);
			parameter.setAliasNames(item.getAliasNames());
		} else if (source instanceof Named) {
			Named named = (Named) source;
			parameter = new Parameter(-1, named.getName(), value);
			parameter.setAliasNames(named.getAliasNames());
		} else {
			parameter = new Parameter(-1, null, value);
		}
		return parameter;
	}

	/**
	 * 使用参数构造对句
	 * 
	 * @param parameters
	 * @param targetTypeDescriptor
	 * @return
	 */
	Object createObject(Parameters parameters, TypeDescriptor targetTypeDescriptor);
}
