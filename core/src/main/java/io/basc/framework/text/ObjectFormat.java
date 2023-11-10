package io.basc.framework.text;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Element;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingFactory;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collect.MultiValueMap;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class ObjectFormat implements PairFormat<String, Value>, MappingFactory {

	@NonNull
	private ConversionService conversionService = Sys.getEnv().getConversionService();

	@Override
	public final String format(Stream<Pair<String, Value>> source) {
		return PairFormat.super.format(source);
	}

	@Override
	public final FormatPosition format(Stream<Pair<String, Value>> source, Appendable target) throws IOException {
		return PairFormat.super.format(source, target);
	}

	@Override
	public final String format(Stream<Pair<String, Value>> source, FormatPosition position) {
		return PairFormat.super.format(source, position);
	}

	protected void formatArray(String sourceKey, Object source, TypeDescriptor sourceType, Appendable target,
			FieldPosition position, ConversionService conversionService) throws IOException {
		TypeDescriptor elementTypeDescriptor = sourceType.getElementTypeDescriptor();
		int len = Array.getLength(source);
		for (int i = 0; i < len; i++) {
			Object value = Array.get(source, i);
			formatValue(sourceKey, value, elementTypeDescriptor, target, position, conversionService);
		}
	}

	@SuppressWarnings("rawtypes")
	protected void formatCollection(String sourceKey, Collection source, TypeDescriptor sourceType, Appendable target,
			FieldPosition position, ConversionService conversionService) throws IOException {
		TypeDescriptor elementTypeDescriptor = sourceType.getElementTypeDescriptor();
		for (Object value : source) {
			formatValue(sourceKey, value, elementTypeDescriptor, target, position, conversionService);
		}
	}

	@Override
	public final String formatMap(Map<? extends String, ? extends Value> sourceMap) {
		return PairFormat.super.formatMap(sourceMap);
	}

	@Override
	public final FormatPosition formatMap(Map<? extends String, ? extends Value> sourceMap, Appendable target)
			throws IOException {
		return PairFormat.super.formatMap(sourceMap, target);
	}

	@Override
	public final String formatMap(Map<? extends String, ? extends Value> sourceMap, FormatPosition position) {
		return PairFormat.super.formatMap(sourceMap, position);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void formatMap(@Nullable String sourceKey, Map source, TypeDescriptor sourceType, Appendable target,
			FieldPosition position, ConversionService conversionService) throws IOException {
		Set<Entry> entrySet = ((Map) source).entrySet();
		for (Entry entry : entrySet) {
			String key = conversionService.convert(entry.getKey(), sourceType.getMapKeyTypeDescriptor(), String.class);
			if (StringUtils.isNotEmpty(sourceKey)) {
				key = joinKey(sourceKey, key);
			}

			Object value = entry.getValue();
			formatValue(key, value, sourceType.getMapValueTypeDescriptor(), target, position, conversionService);
		}
	}

	@Override
	public final String formatMultiValueMap(Map<? extends String, ? extends Collection<? extends Value>> sourceMap) {
		return PairFormat.super.formatMultiValueMap(sourceMap);
	}

	@Override
	public final FormatPosition formatMultiValueMap(
			Map<? extends String, ? extends Collection<? extends Value>> sourceMap, Appendable target)
			throws IOException {
		return PairFormat.super.formatMultiValueMap(sourceMap, target);
	}

	@Override
	public final String formatMultiValueMap(Map<? extends String, ? extends Collection<? extends Value>> sourceMap,
			FormatPosition position) {
		return PairFormat.super.formatMultiValueMap(sourceMap, position);
	}

	public final String formatObject(Object source) {
		return formatObject(source, TypeDescriptor.forObject(source));
	}

	public final FormatPosition formatObject(Object source, Appendable target) throws IOException {
		return formatObject(source, TypeDescriptor.forObject(source), target);
	}

	public final FormatPosition formatObject(Object source, Appendable target, ConversionService conversionService)
			throws IOException {
		return formatObject(source, TypeDescriptor.forObject(source), target, conversionService);
	}

	public final void formatObject(Object source, Appendable target, FormatPosition position) throws IOException {
		formatObject(source, TypeDescriptor.forObject(source), target, position);
	}

	public final void formatObject(Object source, Appendable target, FormatPosition position,
			ConversionService conversionService) throws IOException {
		formatObject(source, TypeDescriptor.forObject(source), target, position, conversionService);
	}

	public final String formatObject(Object source, ConversionService conversionService) {
		return formatObject(source, TypeDescriptor.forObject(source), conversionService);
	}

	public final String formatObject(Object source, FormatPosition position) {
		return formatObject(source, TypeDescriptor.forObject(source), position);
	}

	public final String formatObject(Object source, FormatPosition position, ConversionService conversionService) {
		return formatObject(source, TypeDescriptor.forObject(source), position, conversionService);
	}

	public final String formatObject(Object source, TypeDescriptor sourceType) {
		return formatObject(source, sourceType, getConversionService());
	}

	public final FormatPosition formatObject(Object source, TypeDescriptor sourceType, Appendable target)
			throws IOException {
		return formatObject(source, sourceType, target, getConversionService());
	}

	public final FormatPosition formatObject(Object source, TypeDescriptor sourceType, Appendable target,
			ConversionService conversionService) throws IOException {
		FormatPosition position = new FormatPosition(0);
		formatObject(source, sourceType, target, position, conversionService);
		return position;
	}

	public final void formatObject(Object source, TypeDescriptor sourceType, Appendable target, FormatPosition position)
			throws IOException {
		formatObject(source, sourceType, target, position, getConversionService());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void formatObject(Object source, TypeDescriptor sourceType, Appendable target, FormatPosition position,
			ConversionService conversionService) throws IOException {
		if (sourceType.isMap()) {
			formatMap(null, (Map) source, sourceType, target, position, conversionService);
			return;
		}

		TypeDescriptor targetType = TypeDescriptor.map(Map.class, String.class, Object.class);
		if (conversionService.canConvert(sourceType, targetType)) {
			Map<String, Object> sourceMap = (Map<String, Object>) conversionService.convert(source, sourceType,
					targetType);
			formatMap(null, sourceMap, targetType, target, position, conversionService);
			return;
		}

		// 兜底
		Mapping<?> mapping = getMapping(sourceType.getType());
		for (Element element : mapping.getElements()) {
			String key = element.getName();
			Object value = element.getter().get(source);
			formatValue(key, value, sourceType, target, position, conversionService);
		}
	}

	public final String formatObject(Object source, TypeDescriptor sourceType, ConversionService conversionService) {
		return formatObject(source, sourceType, new FormatPosition(0), conversionService);
	}

	public final String formatObject(Object source, TypeDescriptor sourceType, FormatPosition position) {
		return formatObject(source, sourceType, position, getConversionService());
	}

	public final String formatObject(Object source, TypeDescriptor sourceType, FormatPosition position,
			ConversionService conversionService) {
		StringBuilder sb = new StringBuilder();
		try {
			formatObject(source, sourceType, sb, position, conversionService);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	protected void formatValue(String sourceKey, Object source, TypeDescriptor sourceType, Appendable target,
			FieldPosition position, ConversionService conversionService) throws IOException {
		if (sourceType.isMap()) {
			formatMap(sourceKey, (Map) source, sourceType, target, position, conversionService);
		} else if (sourceType.isCollection()) {
			formatCollection(sourceKey, (Collection) source, sourceType, target, position, conversionService);
		} else if (sourceType.isArray()) {
			formatArray(sourceKey, source, sourceType, target, position, conversionService);
		} else {
			Value value = new AnyValue(source, sourceType, conversionService);
			Pair<String, Value> pair = new Pair<>(sourceKey, value);
			Stream<Pair<String, Value>> stream = Stream.of(pair);
			// 开始format
			FormatPosition nextPosition = new FormatPosition(0);
			nextPosition.setSourcePosition(position);
			format(stream, target, nextPosition);
			position.setBeginIndex(nextPosition.getBeginIndex());
			position.setEndIndex(nextPosition.getEndIndex());
		}
	}

	protected String joinKey(String key1, String key2) {
		return key1 + "." + key2;
	}

	@Override
	public final Stream<Pair<String, Value>> parse(Readable source) throws IOException, ParseException {
		return PairFormat.super.parse(source);
	}

	@Override
	public final Stream<Pair<String, Value>> parse(String source) throws ParseException {
		return PairFormat.super.parse(source);
	}

	@Override
	public final Stream<Pair<String, Value>> parse(String source, ParsePosition position) {
		return PairFormat.super.parse(source, position);
	}

	@Override
	public final Map<String, Value> parseMap(Readable source) throws IOException, ParseException {
		return PairFormat.super.parseMap(source);
	}

	@Override
	public final Map<String, Value> parseMap(String source) throws ParseException {
		return PairFormat.super.parseMap(source);
	}

	@Override
	public final Map<String, Value> parseMap(String source, ParsePosition position) {
		return PairFormat.super.parseMap(source, position);
	}

	@Override
	public final MultiValueMap<String, Value> parseMultiValueMap(Readable source) throws IOException, ParseException {
		return PairFormat.super.parseMultiValueMap(source);
	}

	@Override
	public final MultiValueMap<String, Value> parseMultiValueMap(String source) throws ParseException {
		return PairFormat.super.parseMultiValueMap(source);
	}

	@Override
	public final MultiValueMap<String, Value> parseMultiValueMap(String source, ParsePosition position) {
		return PairFormat.super.parseMultiValueMap(source, position);
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(Readable source, Class<T> targetClass) throws IOException, ParseException {
		return (T) parseObject(source, TypeDescriptor.valueOf(targetClass));
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(Readable source, Class<T> targetClass, ConversionService conversionService)
			throws IOException, ParseException {
		return (T) parseObject(source, TypeDescriptor.valueOf(targetClass), conversionService);
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(Readable source, Class<T> targetClass, ParsePosition position) throws IOException {
		return (T) parseObject(source, TypeDescriptor.valueOf(targetClass), position);
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(Readable source, Class<T> targetClass, ParsePosition position,
			ConversionService conversionService) throws IOException {
		return (T) parseObject(source, TypeDescriptor.valueOf(targetClass), position, conversionService);
	}

	public final Object parseObject(Readable source, TypeDescriptor targetType) throws IOException, ParseException {
		return parseObject(source, targetType, getConversionService());
	}

	public final Object parseObject(Readable source, TypeDescriptor targetType, ConversionService conversionService)
			throws IOException, ParseException {
		ParsePosition pos = new ParsePosition(0);
		Object result = parseObject(source, targetType, pos, conversionService);
		if (pos.getIndex() == 0) {
			throw new ParseException(
					"ObjectFormat.parseObject(Readable source, TypeDescriptor targetType, ConversionService conversionService) failed",
					pos.getErrorIndex());
		}
		return result;
	}

	public final Object parseObject(Readable source, TypeDescriptor targetType, ParsePosition position)
			throws IOException {
		return parseObject(source, targetType, position, getConversionService());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object parseObject(Readable source, TypeDescriptor targetType, ParsePosition position,
			ConversionService conversionService) throws IOException {
		MultiValueMap<String, Value> sourceMap = parseMultiValueMap(source, position);
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
		Mapping<?> mapping = getMapping(targetType.getType());
		for (Entry<String, List<Value>> entry : sourceMap.entrySet()) {
			Element element = mapping.getElements(entry.getKey()).first();
			if (element == null) {
				continue;
			}

			Object value = entry.getValue() != null && entry.getValue().size() == 1 ? entry.getValue().get(0)
					: entry.getValue();
			value = conversionService.convert(value, element.setter().getTypeDescriptor());
			element.setter().set(target, value);
		}
		return target;
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(String source, Class<T> targetClass) throws ParseException {
		return (T) parseObject(source, TypeDescriptor.valueOf(targetClass));
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(String source, Class<T> targetClass, ConversionService conversionService)
			throws ParseException {
		return (T) parseObject(source, TypeDescriptor.valueOf(targetClass), conversionService);
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(String source, Class<T> targetClass, ParsePosition position) {
		return (T) parseObject(source, TypeDescriptor.valueOf(targetClass), position);
	}

	@SuppressWarnings("unchecked")
	public final <T> T parseObject(String source, Class<T> targetClass, ParsePosition position,
			ConversionService conversionService) {
		return (T) parseObject(source, TypeDescriptor.valueOf(targetClass), position, conversionService);
	}

	public final Object parseObject(String source, TypeDescriptor targetType) throws ParseException {
		return parseObject(source, targetType, getConversionService());
	}

	public final Object parseObject(String source, TypeDescriptor targetType, ConversionService conversionService)
			throws ParseException {
		ParsePosition pos = new ParsePosition(0);
		Object result = parseObject(source, targetType, pos, conversionService);
		if (pos.getIndex() == 0) {
			throw new ParseException(
					"ObjectFormat.parseObject(String source, TypeDescriptor targetType, ConversionService conversionService) failed",
					pos.getErrorIndex());
		}
		return result;
	}

	public final Object parseObject(String source, TypeDescriptor targetType, ParsePosition position) {
		return parseObject(source, targetType, position, getConversionService());
	}

	public final Object parseObject(String source, TypeDescriptor targetType, ParsePosition position,
			ConversionService conversionService) {
		StringReader reader = new StringReader(source);
		try {
			return parseObject(reader, targetType, position, conversionService);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		} finally {
			reader.close();
		}
	}

}
