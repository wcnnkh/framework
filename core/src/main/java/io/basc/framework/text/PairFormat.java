package io.basc.framework.text;

import java.io.IOException;
import java.text.FieldPosition;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.strings.StringConverter;
import io.basc.framework.env.Sys;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.Pair;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 键值对格式化
 * 
 * @author wcnnkh
 *
 * @param <V>
 */
@Getter
@Setter
public abstract class PairFormat<V> extends StringConverter implements Format<Stream<Pair<String, V>>> {
	private static final TypeDescriptor MAP_TYPE = TypeDescriptor.map(Map.class, String.class, Object.class);

	@NonNull
	private ConversionService conversionService = Sys.getEnv().getConversionService();

	@Override
	public final void format(Stream<Pair<String, V>> source, Appendable target, FieldPosition position)
			throws IOException {
		format(source, target, position, getConversionService());
	}

	public final String format(Stream<Pair<String, V>> source, ConversionService conversionService) {
		return format(source, new FieldPosition(0), conversionService);
	}

	public final String format(Stream<Pair<String, V>> source, FieldPosition position,
			ConversionService conversionService) {
		StringBuilder sb = new StringBuilder();
		try {
			format(source, sb, position, conversionService);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
		return sb.toString();
	}

	public final void format(Stream<Pair<String, V>> source, Appendable target, ConversionService conversionService)
			throws IOException {
		format(source, target, new FieldPosition(0), conversionService);
	}

	public abstract void format(Stream<Pair<String, V>> source, Appendable target, FieldPosition position,
			ConversionService conversionService) throws IOException;

	public void format(Map<String, ?> sourceMap, Appendable target, FieldPosition position,
			ConversionService conversionService) throws IOException {

	}

	public final Object parseObject(Stream<Pair<String, V>> source, TypeDescriptor targetType) {
		return parseObject(source, targetType, getConversionService());
	}

	public final Object parseObject(Map<String, ?> sourceMap, TypeDescriptor targetType) {
		return parseObject(sourceMap, targetType, getConversionService());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object parseObject(Map<String, ?> sourceMap, TypeDescriptor targetType,
			ConversionService conversionService) {
		if (conversionService.canConvert(MAP_TYPE, targetType)) {
			return conversionService.convert(sourceMap, targetType);
		}

		if (targetType.isMap()) {
			Map map = CollectionFactory.createMap(targetType.getType(), targetType.getMapKeyTypeDescriptor().getType(),
					16);
			for (Entry<String, ?> entry : sourceMap.entrySet()) {
				Object value = conversionService.convert(entry.getValue(), targetType.getMapValueTypeDescriptor());
				map.put(entry.getKey(), value);
			}
			return map;
		}

		// TODO 无法通过conversionService转换，使用反射实现
		return null;
	}

	public Object parseObject(Stream<Pair<String, V>> source, TypeDescriptor targetType,
			ConversionService conversionService) {
		Map<String, List<V>> sourceMap = source
				.collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())));
		return parseObject(sourceMap, targetType, conversionService);
	}
}
