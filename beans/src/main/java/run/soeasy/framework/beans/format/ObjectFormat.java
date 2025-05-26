package run.soeasy.framework.beans.format;

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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.beans.BeanMapping;
import run.soeasy.framework.beans.BeanPropertyDescriptor;
import run.soeasy.framework.beans.BeanUtils;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.CollectionFactory;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.MultiValueMap;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConversionServiceAware;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.mapping.FieldDescriptor;
import run.soeasy.framework.core.type.ReflectionUtils;

@Getter
@Setter
public abstract class ObjectFormat implements PairFormat<String, TypedValue>, ConversionServiceAware {
	@NonNull
	private ConversionService conversionService = SystemConversionService.getInstance();

	@Override
	public final String format(Stream<KeyValue<String, TypedValue>> source) {
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
	public final String formatMap(Map<? extends String, ? extends TypedValue> sourceMap) {
		return PairFormat.super.formatMap(sourceMap);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void formatMap(String sourceKey, Map source, TypeDescriptor sourceType, Appendable target)
			throws IOException {
		Set<Entry> entrySet = ((Map) source).entrySet();
		for (Entry entry : entrySet) {
			String key = (String) conversionService.convert(entry.getKey(), sourceType.getMapKeyTypeDescriptor(),
					TypeDescriptor.valueOf(String.class));
			if (StringUtils.isNotEmpty(sourceKey)) {
				key = joinKey(sourceKey, key);
			}

			Object value = entry.getValue();
			formatValue(key, value, sourceType.getMapValueTypeDescriptor(), target);
		}
	}

	@Override
	public final String formatMultiValueMap(Map<? extends String, ? extends Collection<? extends TypedValue>> sourceMap) {
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
		BeanMapping mapping = BeanUtils.getMapping(sourceType.getType());
		for (FieldDescriptor element : mapping.getElements()) {
			String key = element.getName();
			Object value;
			try {
				value = element.readFrom(source);
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
			TypedValue value = TypedValue.of(source, sourceType);
			KeyValue<String, TypedValue> pair = KeyValue.of(sourceKey, value);
			Stream<KeyValue<String, TypedValue>> stream = Stream.of(pair);
			// 开始format
			format(stream, target);
		}
	}

	protected String joinKey(String key1, String key2) {
		return key1 + "." + key2;
	}

	@Override
	public final Stream<KeyValue<String, TypedValue>> parse(String source) {
		return PairFormat.super.parse(source);
	}

	@Override
	public final Map<String, TypedValue> parseMap(Readable source) throws IOException {
		return PairFormat.super.parseMap(source);
	}

	@Override
	public final Map<String, TypedValue> parseMap(String source) {
		return PairFormat.super.parseMap(source);
	}

	@Override
	public final MultiValueMap<String, TypedValue> parseMultiValueMap(Readable source) throws IOException {
		return PairFormat.super.parseMultiValueMap(source);
	}

	@Override
	public final MultiValueMap<String, TypedValue> parseMultiValueMap(String source) {
		return PairFormat.super.parseMultiValueMap(source);
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(Readable source, Class<T> targetClass) throws IOException {
		return (T) parseObject(source, TypeDescriptor.valueOf(targetClass));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object parseObject(Readable source, TypeDescriptor targetType) throws IOException {
		MultiValueMap<String, TypedValue> sourceMap = parseMultiValueMap(source);
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
			for (Entry<String, List<TypedValue>> entry : sourceMap.entrySet()) {
				Object key = entry.getKey();
				key = conversionService.convert(key, TypeDescriptor.forObject(key),
						targetType.getMapKeyTypeDescriptor());

				Object value = entry.getValue() != null && entry.getValue().size() == 1 ? entry.getValue().get(0)
						: entry.getValue();
				value = conversionService.convert(value, TypeDescriptor.forObject(value),
						targetType.getMapValueTypeDescriptor());

				targetMap.put(key, value);
			}
			return targetMap;
		}

		// 兜底处理
		Object target = ReflectionUtils.newInstance(targetType.getType());
		BeanMapping mapping = BeanUtils.getMapping(targetType.getType());
		for (Entry<String, List<TypedValue>> entry : sourceMap.entrySet()) {
			Elements<BeanPropertyDescriptor> elements = mapping.getValues(entry.getKey());
			for (BeanPropertyDescriptor element : elements) {
				Object value = entry.getValue() != null && entry.getValue().size() == 1 ? entry.getValue().get(0)
						: entry.getValue();
				value = conversionService.convert(value, TypeDescriptor.forObject(value),
						element.getRequiredTypeDescriptor());
				element.writeTo(target, value);
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
