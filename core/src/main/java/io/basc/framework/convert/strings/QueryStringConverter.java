package io.basc.framework.convert.strings;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;

import io.basc.framework.codec.Decoder;
import io.basc.framework.codec.Encoder;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collect.LinkedMultiValueMap;
import io.basc.framework.util.collect.MultiValueMap;
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
			String key = toString(name, conversionService);
			if (Map.class.isAssignableFrom(value.getClass())) {
				Map map = (Map) value;
				writeMap(writeCount, append, key, map, conversionService, handler);
			} else {
				if (conversionService.canConvert(value.getClass(), Map.class)) {
					Map map = conversionService.convert(value, Map.class);
					writeMap(writeCount, append, key, map, conversionService, handler);
				} else {
					String v = toString(value, conversionService);
					handler.write(writeCount, key, v, append);
				}
			}
		}
	}

	private String toString(Object value, ConversionService conversionService) {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void writeMap(LongAdder writeCount, Appendable append, String name, Map map,
			ConversionService conversionService, QueryStringHandler handler) throws IOException {
		Set<Entry<Object, Object>> entrys = map.entrySet();
		for (Entry<Object, Object> entry : entrys) {
			String key = toString(entry.getKey(), conversionService);
			key = StringUtils.isEmpty(name) ? key : (name + "." + key);
			write(writeCount, append, key, entry.getValue(), conversionService, handler);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void write(LongAdder writeCount, Map source, Appendable target, ConversionService conversionService,
			QueryStringHandler handler) throws IOException {
		Assert.requiredArgument(source != null, "source");
		Set<Entry<Object, Object>> entries = source.entrySet();
		for (Entry<Object, Object> entry : entries) {
			write(writeCount, target, entry.getKey(), entry.getValue(), conversionService, handler);
		}
	}

	@SuppressWarnings("rawtypes")
	public final void write(LongAdder writeCount, Map source, Appendable target, ConversionService conversionService)
			throws IOException {
		write(writeCount, source, target, conversionService, handler);
	}

	public final long write(Object source, @Nullable String sourceName, Appendable target,
			ConversionService conversionService, QueryStringHandler handler) throws IOException {
		LongAdder writeCount = new LongAdder();
		write(writeCount, source, sourceName, target, conversionService, handler);
		return writeCount.longValue();
	}

	public final long write(Object source, @Nullable String sourceName, Appendable target,
			ConversionService conversionService) throws IOException {
		return write(source, sourceName, target, conversionService, handler);
	}

	public final String toQueryString(Object form, @Nullable String rootName, ConversionService conversionService,
			QueryStringHandler handler) {
		StringBuilder sb = new StringBuilder();
		try {
			write(new LongAdder(), form, rootName, sb, conversionService, handler);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
		return sb.toString();
	}

	public final String toQueryString(Object form, @Nullable String rootName, ConversionService conversionService) {
		return toQueryString(form, rootName, conversionService, handler);
	}

	public final String toQueryString(Object form, @Nullable String rootName,
			@Nullable Encoder<String, String> encoder) {
		return toQueryString(form, rootName, encoder, getConfiguration());
	}

	public final String toQueryString(Object form, @Nullable Encoder<String, String> encoder) {
		return toQueryString(form, null, encoder);
	}

	public MultiValueMap<String, String> toMultiValueMap(String query, Decoder<String, String> decoder,
			QueryStringConfiguration configuration) {
		MultiValueMap<String, String> parameterMap = new LinkedMultiValueMap<>();
		StringUtils.split(query, configuration.getConnector()).forEach((s) -> {
			String[] kv = StringUtils.splitToArray(s, configuration.getKeyValueConnector());
			if (kv.length == 0) {
				return;
			}

			if (kv.length > 2) {
				return;
			}

			String key = kv[0];
			String value = kv.length == 2 ? kv[1] : null;
			if (decoder != null) {
				key = decoder.decode(key);
				if (value != null) {
					value = decoder.decode(value);
				}
			}
			parameterMap.add(key, value);
		});
		return parameterMap;
	}
}
