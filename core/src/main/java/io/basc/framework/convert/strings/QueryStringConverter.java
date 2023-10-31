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

	public final MultiValueMap<String, Value> parseQueryString(Readable source) throws IOException {
		return parseQueryString(source, getConversionService(), getHandler());
	}

	public final MultiValueMap<String, Value> parseQueryString(Readable source, ConversionService conversionService)
			throws IOException {
		return parseQueryString(source, conversionService, getHandler());
	}

	public final MultiValueMap<String, Value> parseQueryString(Readable source, ConversionService conversionService,
			QueryStringHandler handler) throws IOException {
		MultiValueMap<String, Value> map = new LinkedMultiValueMap<>();
		read(source, conversionService, handler, map::add);
		return map;
	}

	public final MultiValueMap<String, Value> parseQueryString(Readable source, QueryStringHandler handler)
			throws IOException {
		return parseQueryString(source, getConversionService(), handler);
	}

	public final MultiValueMap<String, Value> parseQueryString(String queryString) {
		return parseQueryString(queryString, getConversionService(), getHandler());
	}

	public final MultiValueMap<String, Value> parseQueryString(String queryString,
			ConversionService conversionService) {
		return parseQueryString(queryString, conversionService, getHandler());
	}

	public final MultiValueMap<String, Value> parseQueryString(String queryString, ConversionService conversionService,
			QueryStringHandler handler) {
		Assert.requiredArgument(queryString != null, "queryString");
		StringReader reader = new StringReader(queryString);
		try {
			return parseQueryString(reader, conversionService, handler);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		} finally {
			reader.close();
		}
	}

	public final MultiValueMap<String, Value> parseQueryString(String queryString, QueryStringHandler handler) {
		return parseQueryString(queryString, getConversionService(), handler);
	}

	public final void read(LongAdder readCount, Readable source, BiConsumer<String, ? super Value> consumer)
			throws IOException {
		read(readCount, source, getConversionService(), getHandler(), consumer);
	}

	public final void read(LongAdder readCount, Readable source, ConversionService conversionService,
			BiConsumer<String, ? super Value> consumer) throws IOException {
		read(readCount, source, conversionService, getHandler(), consumer);
	}

	public final void read(LongAdder readCount, Readable source, ConversionService conversionService,
			QueryStringHandler handler, BiConsumer<String, ? super Value> consumer) throws IOException {
		handler.read(readCount, source, (key, value) -> {
			AnyValue anyValue = new AnyValue(value, conversionService);
			anyValue.setStringConverter(this);
			consumer.accept(key, anyValue);
		});
	}

	public final void read(LongAdder readCount, Readable source, QueryStringHandler handler,
			BiConsumer<String, ? super Value> consumer) throws IOException {
		read(readCount, source, getConversionService(), handler, consumer);
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
		LongAdder readCount = new LongAdder();
		read(readCount, source, conversionService, handler, consumer);
		return readCount.longValue();
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
		LongAdder writeCount = new LongAdder();
		StringBuilder sb = new StringBuilder();
		try {
			write(writeCount, source, sb, conversionService, handler);
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
	private void write(LongAdder writeCount, Appendable append, Object name, Object value,
			ConversionService conversionService, QueryStringHandler handler) throws IOException {
		if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			for (int i = 0; i < len; i++) {
				Object item = Array.get(value, i);
				write(writeCount, append, name, item, conversionService, handler);
			}
		} else if (Collection.class.isAssignableFrom(value.getClass())) {
			Collection collection = (Collection) value;
			for (Object item : collection) {
				write(writeCount, append, name, item, conversionService, handler);
			}
		} else {
			String key = convertToString(name, conversionService);
			if (Map.class.isAssignableFrom(value.getClass())) {
				Map map = (Map) value;
				writeMap(writeCount, append, key, map, conversionService, handler);
			} else {
				if (conversionService.canConvert(value.getClass(), Map.class)) {
					Map map = conversionService.convert(value, Map.class);
					writeMap(writeCount, append, key, map, conversionService, handler);
				} else {
					String v = convertToString(value, conversionService);
					handler.write(writeCount, key, v, append);
				}
			}
		}
	}

	public final void write(LongAdder writeCount, Object source, Appendable target) throws IOException {
		write(writeCount, source, target, getConversionService(), getHandler());
	}

	public final void write(LongAdder writeCount, Object source, Appendable target, ConversionService conversionService)
			throws IOException {
		write(writeCount, source, target, conversionService, getHandler());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void write(LongAdder writeCount, Object source, Appendable target, ConversionService conversionService,
			QueryStringHandler handler) throws IOException {
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
			write(writeCount, target, entry.getKey(), entry.getValue(), conversionService, handler);
		}
	}

	public final void write(LongAdder writeCount, Object source, Appendable target, QueryStringHandler handler)
			throws IOException {
		write(writeCount, source, target, getConversionService(), handler);
	}

	public final long write(Object source, Appendable target) throws IOException {
		return write(source, target, getConversionService(), getHandler());
	}

	public final long write(Object source, Appendable target, ConversionService conversionService) throws IOException {
		return write(source, target, conversionService, getHandler());
	}

	public final long write(Object source, Appendable target, ConversionService conversionService,
			QueryStringHandler handler) throws IOException {
		LongAdder writeCount = new LongAdder();
		write(writeCount, source, target, conversionService, handler);
		return writeCount.longValue();
	}

	public final long write(Object source, Appendable target, QueryStringHandler handler) throws IOException {
		return write(source, target, getConversionService(), handler);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void writeMap(LongAdder writeCount, Appendable append, String name, Map map,
			ConversionService conversionService, QueryStringHandler handler) throws IOException {
		Set<Entry<Object, Object>> entrys = map.entrySet();
		for (Entry<Object, Object> entry : entrys) {
			String key = convertToString(entry.getKey(), conversionService);
			key = StringUtils.isEmpty(name) ? key : (name + "." + key);
			write(writeCount, append, key, entry.getValue(), conversionService, handler);
		}
	}
}
