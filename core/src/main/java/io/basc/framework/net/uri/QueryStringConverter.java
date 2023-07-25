package io.basc.framework.net.uri;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import io.basc.framework.codec.Decoder;
import io.basc.framework.codec.Encoder;
import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.strings.StringConverter;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collect.LinkedMultiValueMap;
import io.basc.framework.util.collect.MultiValueMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
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

	@SuppressWarnings({ "rawtypes" })
	private void appendQueryString(StringBuilder append, String name, Object value, Encoder<String, String> encoder,
			ConversionService conversionService) {
		if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			for (int i = 0; i < len; i++) {
				Object item = Array.get(value, i);
				appendQueryString(append, name, item, encoder, conversionService);
			}
		} else if (Collection.class.isAssignableFrom(value.getClass())) {
			Collection collection = (Collection) value;
			for (Object item : collection) {
				appendQueryString(append, name, item, encoder, conversionService);
			}
		} else if (Map.class.isAssignableFrom(value.getClass())) {
			Map map = (Map) value;
			appendQueryString(append, name, map, encoder, conversionService);
		} else {
			if (conversionService.canConvert(value.getClass(), Map.class)) {
				Map map = conversionService.convert(value, Map.class);
				appendQueryString(append, name, map, encoder, conversionService);
			} else {
				String k = name;
				String v;
				if (canConvert(value.getClass(), String.class)) {
					v = invert(value, String.class);
				} else if (conversionService.canConvert(value.getClass(), String.class)) {
					v = conversionService.convert(value, String.class);
				} else {
					v = value.toString();
				}

				if (encoder != null) {
					k = encoder.encode(k);
					v = encoder.encode(v);
				}

				if (append.length() > 0) {
					append.append("&");
				}
				append.append(k);
				append.append("=");
				append.append(v);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void appendQueryString(StringBuilder append, String name, Map map, Encoder<String, String> encoder,
			ConversionService conversionService) {
		map.forEach((k, v) -> {
			String key;
			if (canConvert(k.getClass(), String.class)) {
				key = invert(v, String.class);
			} else if (conversionService.canConvert(v.getClass(), String.class)) {
				key = conversionService.convert(v, String.class);
			} else {
				key = v.toString();
			}
			key = StringUtils.isEmpty(name) ? key : (name + "." + k);
			appendQueryString(append, key, v, encoder, conversionService);
		});
	}

	public String toQueryString(Object form, @Nullable String rootName, @Nullable Encoder<String, String> encoder,
			ConversionService conversionService) {
		Assert.requiredArgument(form != null, "form");
		if (form instanceof Map && conversionService.canConvert(form.getClass(), Map.class)) {
			StringBuilder sb = new StringBuilder();
			appendQueryString(sb, rootName, form, encoder, conversionService);
			return sb.toString();
		}
		throw new ConversionFailedException(TypeDescriptor.forObject(form), TypeDescriptor.valueOf(String.class), form,
				null);
	}

	public final String toQueryString(Object form, @Nullable String rootName,
			@Nullable Encoder<String, String> encoder) {
		return toQueryString(form, rootName, encoder, Sys.getEnv().getConversionService());
	}

	public final String toQueryString(Object form, @Nullable Encoder<String, String> encoder) {
		return toQueryString(form, null, encoder);
	}

	public MultiValueMap<String, String> parseFormParameters(String query, Decoder<String, String> decoder) {
		MultiValueMap<String, String> parameterMap = new LinkedMultiValueMap<>();
		StringUtils.split(query, "&").forEach((s) -> {
			String[] kv = StringUtils.splitToArray(s, "=");
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
