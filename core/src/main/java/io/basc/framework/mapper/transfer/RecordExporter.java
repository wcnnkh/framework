package io.basc.framework.mapper.transfer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.execution.Parameter;
import io.basc.framework.execution.Parameters;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.stereotype.Mapping;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Item;
import io.basc.framework.util.Items;
import io.basc.framework.util.Named;
import io.basc.framework.util.element.Elements;

public interface RecordExporter extends Exporter {
	@SuppressWarnings("unchecked")
	@Override
	default void doWrite(Object data, TypeDescriptor typeDescriptor) throws IOException {
		if (data instanceof Parameters) {
			Parameters parameters = (Parameters) data;
			doWriteParameters(parameters);
		} else if (getMapper().canConvert(typeDescriptor, Parameters.class)) {
			Parameters parameters = getMapper().convert(data, typeDescriptor, Parameters.class);
			doWriteParameters(parameters);
		} else if (data instanceof Items) {
			Items<?> items = (Items<?>) data;
			doWriteItems(items, typeDescriptor);
		} else if (typeDescriptor.isArray()) {
			doWriteArray(data, typeDescriptor.getElementTypeDescriptor());
		} else if (typeDescriptor.isCollection()) {
			Collection<?> collection = (Collection<?>) data;
			doWriteIterable(collection, typeDescriptor.getElementTypeDescriptor());
		} else if (data instanceof Iterable) {
			Iterable<?> iterable = (Iterable<?>) data;
			doWriteIterable(iterable, typeDescriptor.getGeneric(0));
		} else if (data instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) data;
			doWriteMap(map, typeDescriptor.getMapKeyTypeDescriptor(), typeDescriptor.getMapValueTypeDescriptor());
		} else if (getMapper().isEntity(typeDescriptor)) {
			Mapping<?> mapping = getMapper().getMapping(typeDescriptor.getType());
			doWriteEntity(data, typeDescriptor, mapping);
		} else {
			TypeDescriptor mapTypeDescriptor = TypeDescriptor.map(Map.class, String.class, Object.class);
			if (getMapper().canConvert(typeDescriptor, mapTypeDescriptor)) {
				Map<String, Object> map = (Map<String, Object>) getMapper().convert(data, typeDescriptor,
						mapTypeDescriptor);
				doWriteMap(map, mapTypeDescriptor.getMapKeyTypeDescriptor(),
						mapTypeDescriptor.getMapValueTypeDescriptor());
			} else {
				Parameter parameter = encapsulation(data, typeDescriptor);
				doWriteParameters(new Parameters(parameter));
			}
		}
	}

	/**
	 * 底层调用{@link #doWriteParameters(Parameters)}
	 * 
	 * @param array
	 * @param elementTypeDescriptor
	 * @throws IOException
	 */
	default void doWriteArray(Object array, TypeDescriptor elementTypeDescriptor) throws IOException {
		Parameter[] parameters = new Parameter[Array.getLength(array)];
		for (int len = parameters.length, i = 0; i < len; i++) {
			Object value = Array.get(array, i);
			Parameter parameter = encapsulation(value, elementTypeDescriptor);
			parameter.setPositionIndex(i);
			parameters[i] = parameter;
		}
		doWriteParameters(new Parameters(parameters));
	}

	/**
	 * 底层调用{@link #doWriteParameters(Parameters)}
	 * 
	 * @param entity
	 * @param typeDescriptor
	 * @param mapping
	 * @throws IOException
	 */
	default void doWriteEntity(Object entity, TypeDescriptor typeDescriptor, Mapping<?> mapping) throws IOException {
		Elements<Parameter> elements = mapping.getElements().filter((e) -> e.isSupportGetter())
				.map((fieldDescriptor) -> {
					Object value = fieldDescriptor.getter().get(entity);
					Parameter parameter = new Parameter(fieldDescriptor.getPositionIndex(), fieldDescriptor.getName(),
							value, fieldDescriptor.getter().getTypeDescriptor());
					parameter.setAliasNames(fieldDescriptor.getAliasNames());
					return parameter;
				});
		doWriteParameters(new Parameters(elements));
	}

	/**
	 * 底层调用{@link #doWriteParameters(Parameters)}
	 * 
	 * @param items
	 * @param elementTypeDescriptor
	 * @throws IOException
	 */
	default void doWriteItems(Items<?> items, TypeDescriptor elementTypeDescriptor) throws IOException {
		Elements<Parameter> elements = items.getElements().map((e) -> encapsulation(e, elementTypeDescriptor));
		doWriteParameters(new Parameters(elements));
	}

	/**
	 * 底层调用{@link #doWriteParameters(Parameters)}
	 * 
	 * @param iterable
	 * @param elementTypeDescriptor
	 * @throws IOException
	 */
	default void doWriteIterable(Iterable<?> iterable, TypeDescriptor elementTypeDescriptor) throws IOException {
		Elements<?> elements = Elements.of(iterable);
		Elements<Parameter> parameters = elements.map((e) -> {
			return encapsulation(e, elementTypeDescriptor);
		});
		doWriteParameters(new Parameters(parameters));
	}

	/**
	 * 底层调用{@link #doWriteParameters(Parameters)}
	 * 
	 * @param map
	 * @param keyTypeDescriptor
	 * @param valueTypeDescriptor
	 * @throws IOException
	 */
	default void doWriteMap(Map<?, ?> map, TypeDescriptor keyTypeDescriptor, TypeDescriptor valueTypeDescriptor)
			throws IOException {
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
		doWriteParameters(new Parameters(list));
	}

	void doWriteParameters(Parameters parameters) throws IOException;

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

	ObjectMapper getMapper();
}
