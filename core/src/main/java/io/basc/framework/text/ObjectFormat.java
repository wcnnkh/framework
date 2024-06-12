package io.basc.framework.text;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import io.basc.framework.beans.BeanUtils;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ObjectValue;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.convert.support.GlobalConversionService;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.stereotype.FieldDescriptor;
import io.basc.framework.mapper.stereotype.Mapping;
import io.basc.framework.mapper.stereotype.MappingFactory;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collect.MultiValueMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class ObjectFormat implements PairFormat<String, Value> {
	@NonNull
	private ConversionService conversionService = GlobalConversionService.getInstance();
	@NonNull
	private MappingFactory mappingFactory = BeanUtils.getMapper();

	@Override
	public final String format(Stream<Pair<String, Value>> source) {
		return PairFormat.super.format(source);
	}

	protected void formatArray(String sourceKey, Object source, TypeDescriptor sourceType, Appendable target)
			throws IOException {
		TypeDescriptor elementTypeDescriptor = sourceType.getElementTypeDescriptor();
		int len = Array.getLength(source);
		for (int i = 0; i < len; i++) {
			Object value = Array.get(source, i);
			formatValue(sourceKey, value, elementTypeDescriptor, target);
		}
	}

	@SuppressWarnings("rawtypes")
	protected void formatCollection(String sourceKey, Collection source, TypeDescriptor sourceType, Appendable target)
			throws IOException {
		TypeDescriptor elementTypeDescriptor = sourceType.getElementTypeDescriptor();
		for (Object value : source) {
			formatValue(sourceKey, value, elementTypeDescriptor, target);
		}
	}

	@Override
	public final String formatMap(Map<? extends String, ? extends Value> sourceMap) {
		return PairFormat.super.formatMap(sourceMap);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void formatMap(@Nullable String sourceKey, Map source, TypeDescriptor sourceType, Appendable target)
			throws IOException {
		Set<Entry> entrySet = ((Map) source).entrySet();
		for (Entry entry : entrySet) {
			String key = conversionService.convert(entry.getKey(), sourceType.getMapKeyTypeDescriptor(), String.class);
			if (StringUtils.isNotEmpty(sourceKey)) {
				key = joinKey(sourceKey, key);
			}

			Object value = entry.getValue();
			formatValue(key, value, sourceType.getMapValueTypeDescriptor(), target);
		}
	}

	@Override
	public final String formatMultiValueMap(Map<? extends String, ? extends Collection<? extends Value>> sourceMap) {
		return PairFormat.super.formatMultiValueMap(sourceMap);
	}

	public final String formatObject(Object source) {
		return formatObject(source, TypeDescriptor.forObject(source));
	}

	public final String formatObject(Object source, TypeDescriptor sourceType) {
		StringBuilder sb = new StringBuilder();
		try {
			formatObject(source, sourceType, sb);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
		return sb.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void formatObject(Object source, TypeDescriptor sourceType, Appendable target) throws IOException {
		if (sourceType.isMap()) {
			formatMap(null, (Map) source, sourceType, target);
			return;
		}

		TypeDescriptor targetType = TypeDescriptor.map(Map.class, String.class, Object.class);
		if (conversionService.canConvert(sourceType, targetType)) {
			Map<String, Object> sourceMap = (Map<String, Object>) conversionService.convert(source, sourceType,
					targetType);
			formatMap(null, sourceMap, targetType, target);
			return;
		}

		// 兜底
		Mapping<?> mapping = mappingFactory.getMapping(sourceType.getType());
		for (FieldDescriptor element : mapping.getElements()) {
			String key = element.getName();
			Object value;
			try {
				value = element.getter().get(source);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
			formatValue(key, value, sourceType, target);
		}
	}

	@SuppressWarnings("rawtypes")
	protected void formatValue(String sourceKey, Object source, TypeDescriptor sourceType, Appendable target)
			throws IOException {
		if (sourceType.isMap()) {
			formatMap(sourceKey, (Map) source, sourceType, target);
		} else if (sourceType.isCollection()) {
			formatCollection(sourceKey, (Collection) source, sourceType, target);
		} else if (sourceType.isArray()) {
			formatArray(sourceKey, source, sourceType, target);
		} else {
			Value value = new ObjectValue(source, sourceType);
			Pair<String, Value> pair = new Pair<>(sourceKey, value);
			Stream<Pair<String, Value>> stream = Stream.of(pair);
			// 开始format
			format(stream, target);
		}
	}

	protected String joinKey(String key1, String key2) {
		return key1 + "." + key2;
	}

	@Override
	public final Stream<Pair<String, Value>> parse(String source) {
		return PairFormat.super.parse(source);
	}

	@Override
	public final Map<String, Value> parseMap(Readable source) throws IOException {
		return PairFormat.super.parseMap(source);
	}

	@Override
	public final Map<String, Value> parseMap(String source) {
		return PairFormat.super.parseMap(source);
	}

	@Override
	public final MultiValueMap<String, Value> parseMultiValueMap(Readable source) throws IOException {
		return PairFormat.super.parseMultiValueMap(source);
	}

	@Override
	public final MultiValueMap<String, Value> parseMultiValueMap(String source) {
		return PairFormat.super.parseMultiValueMap(source);
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(Readable source, Class<T> targetClass) throws IOException {
		return (T) parseObject(source, TypeDescriptor.valueOf(targetClass));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object parseObject(Readable source, TypeDescriptor targetType) throws IOException {
		MultiValueMap<String, Value> sourceMap = parseMultiValueMap(source);
		TypeDescriptor sourceType = TypeDescriptor.map(Map.class, TypeDescriptor.valueOf(String.class),
				TypeDescriptor.collection(List.class, String.class));
		if (conversionService.canConvert(sourceType, targetType)) {
			return conversionService.convert(sourceMap, sourceType, targetType);
		}

		if (targetType.isMap()) {
			if (CollectionUtils.isEmpty(sourceMap)) {
				return Collections.emptyMap();
			}

			Map targetMap = CollectionFactory.createMap(targetType.getType(),
					targetType.getMapKeyTypeDescriptor().getType(), sourceMap.size());
			for (Entry<String, List<Value>> entry : sourceMap.entrySet()) {
				Object key = entry.getKey();
				key = conversionService.convert(key, targetType.getMapKeyTypeDescriptor());

				Object value = entry.getValue() != null && entry.getValue().size() == 1 ? entry.getValue().get(0)
						: entry.getValue();
				value = conversionService.convert(value, targetType.getMapValueTypeDescriptor());

				targetMap.put(key, value);
			}
			return targetMap;
		}

		// 兜底处理
		Object target = ReflectionUtils.newInstance(targetType.getType());
		Mapping<?> mapping = mappingFactory.getMapping(targetType.getType());
		for (Entry<String, List<Value>> entry : sourceMap.entrySet()) {
			FieldDescriptor element = mapping.getElements(entry.getKey()).first();
			if (element == null) {
				continue;
			}

			Object value = entry.getValue() != null && entry.getValue().size() == 1 ? entry.getValue().get(0)
					: entry.getValue();
			value = conversionService.convert(value, element.setter().getTypeDescriptor());
			try {
				element.setter().set(target, value);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return target;
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(String source, Class<T> targetClass) {
		return (T) parseObject(source, TypeDescriptor.valueOf(targetClass));
	}

	public final Object parseObject(String source, TypeDescriptor targetType) {
		StringReader reader = new StringReader(source);
		try {
			return parseObject(reader, targetType);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		} finally {
			reader.close();
		}
	}
}
