package io.basc.framework.convert.strings;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;

import io.basc.framework.codec.support.URLCodec;
import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collect.LinkedMultiValueMap;
import io.basc.framework.util.collect.MultiValueMap;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QueryStringConverter extends StringConverter {
	private static final TypeDescriptor MAP_TYPE = TypeDescriptor.map(Map.class, String.class, Object.class);

	private static volatile QueryStringConverter instance;

	public static QueryStringConverter getInstance() {
		if (instance == null) {
			synchronized (QueryStringConverter.class) {
				if (instance == null) {
					instance = new QueryStringConverter();
				}
			}
		}
		return instance;
	}

	@NonNull
	private QueryStringHandler handler = new DefaultQueryStringHandler();
	@NonNull
	private ConversionService conversionService = Sys.getEnv().getConversionService();

	private Object convert(String source, TypeDescriptor targetType, ConversionService conversionService) {
		if (canConvert(String.class, targetType)) {
			return convert(source, targetType);
		}

		if (conversionService.canConvert(String.class, targetType)) {
			return conversionService.convert(source, targetType);
		}

		return StringConverter.DEFAULT.convert(source, targetType);
	}

	private String convertToString(Object value, ConversionService conversionService) {
		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			return (String) value;
		}

		if (canConvert(value.getClass(), String.class)) {
			return invert(value, String.class);
		} else if (conversionService.canConvert(value.getClass(), String.class)) {
			return conversionService.convert(value, String.class);
		} else {
			return value.toString();
		}
	}

	public final MultiValueMap<String, Value> parseMultiValueMap(Readable source) throws IOException {
		return parseMultiValueMap(source, getConversionService(), getHandler());
	}

	public final MultiValueMap<String, Value> parseMultiValueMap(Readable source, ConversionService conversionService)
			throws IOException {
		return parseMultiValueMap(source, conversionService, getHandler());
	}

	public final MultiValueMap<String, Value> parseMultiValueMap(Readable source, ConversionService conversionService,
			QueryStringHandler handler) throws IOException {
		MultiValueMap<String, Value> map = new LinkedMultiValueMap<>();
		read(source, conversionService, handler, map::add);
		return map;
	}

	public final MultiValueMap<String, Value> parseMultiValueMap(Readable source, QueryStringHandler handler)
			throws IOException {
		return parseMultiValueMap(source, getConversionService(), handler);
	}

	public final MultiValueMap<String, Value> parseMultiValueMap(String queryString) {
		return parseMultiValueMap(queryString, getConversionService(), getHandler());
	}

	public final MultiValueMap<String, Value> parseMultiValueMap(String queryString,
			ConversionService conversionService) {
		return parseMultiValueMap(queryString, conversionService, getHandler());
	}

	public final MultiValueMap<String, Value> parseMultiValueMap(String queryString,
			ConversionService conversionService, QueryStringHandler handler) {
		Assert.requiredArgument(queryString != null, "queryString");
		StringReader reader = new StringReader(queryString);
		try {
			return parseMultiValueMap(reader, conversionService, handler);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		} finally {
			reader.close();
		}
	}

	public final MultiValueMap<String, Value> parseMultiValueMap(String queryString, QueryStringHandler handler) {
		return parseMultiValueMap(queryString, getConversionService(), handler);
	}

	public final Object parseObject(LongAdder readSize, Readable readable, TypeDescriptor targetType)
			throws IOException {
		return parseObject(readSize, readable, targetType, getConversionService(), getHandler());
	}

	public final Object parseObject(LongAdder readSize, Readable readable, TypeDescriptor targetType,
			ConversionService conversionService) throws IOException {
		return parseObject(readSize, readable, targetType, conversionService, getHandler());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final Object parseObject(LongAdder readSize, Readable readable, TypeDescriptor targetType,
			ConversionService conversionService, QueryStringHandler handler) throws IOException {
		if (targetType.isMap()) {
			Map map = CollectionFactory.createMap(targetType.getType(), targetType.getMapKeyTypeDescriptor().getType(),
					16);
			read(readSize, readable, conversionService, handler, (k, v) -> {
				map.put(convert(k, targetType.getMapKeyTypeDescriptor(), conversionService),
						convert(v.getAsString(), targetType.getMapValueTypeDescriptor(), conversionService));
			});
			return map;
		}

		if (conversionService.canConvert(MAP_TYPE, targetType)) {
			// 可以通过map转换为对象
			MultiValueMap<String, Value> multiValueMap = parseMultiValueMap(readable, conversionService, handler);
			return conversionService.convert(multiValueMap, MAP_TYPE, targetType);
		}

		// TODO 无法通过conversionService转换，使用反射实现
		return null;
	}

	public final Object parseObject(LongAdder readSize, Readable readable, TypeDescriptor targetType,
			QueryStringHandler handler) throws IOException {
		return parseObject(readSize, readable, targetType, getConversionService(), handler);
	}

	public final Object parseObject(String queryString, TypeDescriptor targetType) {
		return parseObject(queryString, targetType, getConversionService(), getHandler());
	}

	public final Object parseObject(String queryString, TypeDescriptor targetType,
			ConversionService conversionService) {
		return parseObject(queryString, targetType, conversionService, getHandler());
	}

	public final Object parseObject(String queryString, TypeDescriptor targetType, ConversionService conversionService,
			QueryStringHandler handler) {
		StringReader stringReader = new StringReader(queryString);
		try {
			return parseObject(new LongAdder(), stringReader, targetType, conversionService);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
	}

	public final Object parseObject(String queryString, TypeDescriptor targetType, QueryStringHandler handler) {
		return parseObject(queryString, targetType, getConversionService(), handler);
	}

	public final void read(LongAdder readSize, Readable source, BiConsumer<String, ? super Value> consumer)
			throws IOException {
		read(readSize, source, getConversionService(), getHandler(), consumer);
	}

	public final void read(LongAdder readSize, Readable source, ConversionService conversionService,
			BiConsumer<String, ? super Value> consumer) throws IOException {
		read(readSize, source, conversionService, getHandler(), consumer);
	}

	/**
	 * 已读取的大小
	 * 
	 * @param readSize
	 * @param source
	 * @param conversionService
	 * @param handler
	 * @param consumer
	 * @throws IOException
	 */
	public final void read(LongAdder readSize, Readable source, ConversionService conversionService,
			QueryStringHandler handler, BiConsumer<String, ? super Value> consumer) throws IOException {
		handler.read(readSize, source, (key, value) -> {
			AnyValue anyValue = new AnyValue(value, conversionService);
			anyValue.setStringConverter(this);
			consumer.accept(key, anyValue);
		});
	}

	public final void read(LongAdder readSize, Readable source, QueryStringHandler handler,
			BiConsumer<String, ? super Value> consumer) throws IOException {
		read(readSize, source, getConversionService(), handler, consumer);
	}

	public final long read(Readable source, BiConsumer<String, ? super Value> consumer) throws IOException {
		return read(source, getConversionService(), getHandler(), consumer);
	}

	public final long read(Readable source, ConversionService conversionService,
			BiConsumer<String, ? super Value> consumer) throws IOException {
		return read(source, conversionService, getHandler(), consumer);
	}

	public final long read(Readable source, ConversionService conversionService, QueryStringHandler handler,
			BiConsumer<String, ? super Value> consumer) throws IOException {
		LongAdder readSize = new LongAdder();
		read(readSize, source, conversionService, handler, consumer);
		return readSize.longValue();
	}

	public final long read(Readable source, QueryStringHandler handler, BiConsumer<String, ? super Value> consumer)
			throws IOException {
		return read(source, getConversionService(), handler, consumer);
	}

	public final String toQueryString(Object source) {
		return toQueryString(source, getConversionService(), getHandler());
	}

	public final String toQueryString(Object source, ConversionService conversionService) {
		return toQueryString(source, conversionService, getHandler());
	}

	public final String toQueryString(Object source, ConversionService conversionService, QueryStringHandler handler) {
		LongAdder writtenSize = new LongAdder();
		StringBuilder sb = new StringBuilder();
		try {
			write(writtenSize, source, sb, conversionService, handler);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
		return sb.toString();
	}

	public final String toQueryString(Object source, QueryStringHandler handler) {
		return toQueryString(source, getConversionService(), handler);
	}

	public final String toUrlQueryString(Object source, Charset charset) {
		DefaultQueryStringHandler queryStringHandler = new DefaultQueryStringHandler();
		if (charset != null) {
			queryStringHandler.setCodec(new URLCodec(charset));
		}
		return toQueryString(source, queryStringHandler);
	}

	@SuppressWarnings({ "rawtypes" })
	private void write(LongAdder writtenSize, Appendable append, Object name, Object value,
			ConversionService conversionService, QueryStringHandler handler) throws IOException {
		if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			for (int i = 0; i < len; i++) {
				Object item = Array.get(value, i);
				write(writtenSize, append, name, item, conversionService, handler);
			}
		} else if (Collection.class.isAssignableFrom(value.getClass())) {
			Collection collection = (Collection) value;
			for (Object item : collection) {
				write(writtenSize, append, name, item, conversionService, handler);
			}
		} else {
			String key = convertToString(name, conversionService);
			if (Map.class.isAssignableFrom(value.getClass())) {
				Map map = (Map) value;
				writeMap(writtenSize, append, key, map, conversionService, handler);
			} else {
				if (conversionService.canConvert(value.getClass(), Map.class)) {
					Map map = conversionService.convert(value, Map.class);
					writeMap(writtenSize, append, key, map, conversionService, handler);
				} else {
					String v = convertToString(value, conversionService);
					handler.write(writtenSize, key, v, append);
				}
			}
		}
	}

	public final void write(LongAdder writtenSize, Object source, Appendable target) throws IOException {
		write(writtenSize, source, target, getConversionService(), getHandler());
	}

	public final void write(LongAdder writtenSize, Object source, Appendable target,
			ConversionService conversionService) throws IOException {
		write(writtenSize, source, target, conversionService, getHandler());
	}

	/**
	 * 写入
	 * 
	 * @param writtenSize       已写入大小
	 * @param source
	 * @param target
	 * @param conversionService
	 * @param handler
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void write(LongAdder writtenSize, Object source, Appendable target,
			ConversionService conversionService, QueryStringHandler handler) throws IOException {
		Assert.requiredArgument(source != null, "source");
		Map map;
		if (source instanceof Map) {
			map = (Map) source;
		} else if (conversionService.canConvert(source.getClass(), Map.class)) {
			map = conversionService.convert(source, Map.class);
		} else {
			throw new ConversionFailedException(TypeDescriptor.forObject(source), TypeDescriptor.valueOf(Map.class),
					source, null);
		}

		Set<Entry<Object, Object>> entries = map.entrySet();
		for (Entry<Object, Object> entry : entries) {
			write(writtenSize, target, entry.getKey(), entry.getValue(), conversionService, handler);
		}
	}

	public final void write(LongAdder writtenSize, Object source, Appendable target, QueryStringHandler handler)
			throws IOException {
		write(writtenSize, source, target, getConversionService(), handler);
	}

	public final long write(Object source, Appendable target) throws IOException {
		return write(source, target, getConversionService(), getHandler());
	}

	public final long write(Object source, Appendable target, ConversionService conversionService) throws IOException {
		return write(source, target, conversionService, getHandler());
	}

	public final long write(Object source, Appendable target, ConversionService conversionService,
			QueryStringHandler handler) throws IOException {
		LongAdder writtenSize = new LongAdder();
		write(writtenSize, source, target, conversionService, handler);
		return writtenSize.longValue();
	}

	public final long write(Object source, Appendable target, QueryStringHandler handler) throws IOException {
		return write(source, target, getConversionService(), handler);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void writeMap(LongAdder writtenSize, Appendable append, String name, Map map,
			ConversionService conversionService, QueryStringHandler handler) throws IOException {
		Set<Entry<Object, Object>> entrys = map.entrySet();
		for (Entry<Object, Object> entry : entrys) {
			String key = convertToString(entry.getKey(), conversionService);
			key = StringUtils.isEmpty(name) ? key : (name + "." + key);
			write(writtenSize, append, key, entry.getValue(), conversionService, handler);
		}
	}
}
