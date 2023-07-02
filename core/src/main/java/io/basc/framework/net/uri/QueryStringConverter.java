package io.basc.framework.net.uri;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.codec.Encoder;
import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.strings.StringConverter;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QueryStringConverter extends StringConverter implements Codec<Map<String, String>, String> {
	private final Codec<String, String> keyCodec;
	private final Codec<String, String> valueCodec;
	private final ConversionService conversionService;

	@Override
	public Object convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (isConverterRegistred(targetType.getType())) {
			return super.convert(source, sourceType, targetType);
		}

		Map<String, String> map = decode(source);
		return conversionService.convert(map, TypeDescriptor.map(Map.class, String.class, String.class), targetType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String invert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (isInverterRegistred(sourceType.getType())) {
			return super.invert(source, sourceType, targetType);
		}

		Map<String, String> map = (Map<String, String>) conversionService.convert(source, sourceType,
				TypeDescriptor.map(Map.class, String.class, String.class));
		return encode(map);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String toQueryString(StringBuilder append, String parentName, Map parameterMap,
			Encoder<String, String> keyEncoder, Encoder<String, String> valueEncoder) {
		StringBuilder sb = new StringBuilder();
		Iterator<Entry> iterator = parameterMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = iterator.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (key == null || value == null) {
				continue;
			}

			String encodeKey = key instanceof String ? (String) key : invert(key, String.class);
			if (keyEncoder != null) {
				encodeKey = keyEncoder.encode(encodeKey);
			}

			if (value.getClass().isArray()) {

			} else if (Collection.class.isAssignableFrom(value.getClass())) {

			} else if (Map.class.isAssignableFrom(value.getClass())) {

			}

			sb.append(key);
			sb.append("=");

			String value = entry.getValue();
			value = value == null ? null : valueCodec.encode(value);
			if (value != null) {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append("&");
			}
		}
		return sb.toString();
	}

	@Override
	public String encode(Map<String, String> source) throws EncodeException {
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, String>> iterator = source.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			String key = entry.getKey();
			key = keyCodec.encode(key);
			sb.append(key);
			sb.append("=");
			String value = entry.getValue();
			value = value == null ? null : valueCodec.encode(value);
			if (value != null) {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append("&");
			}
		}
		return sb.toString();
	}

	@Override
	public Map<String, String> decode(String source) throws DecodeException {
		Map<String, String> map = new LinkedHashMap<>();
		StringUtils.split(source, "&").forEach((s) -> {
			String[] kv = StringUtils.splitToArray(s, "=");
			if (kv.length == 0) {
				return;
			}

			if (kv.length > 2) {
				return;
			}

			String key = kv[0];
			key = keyCodec.decode(key);
			String value = kv.length == 2 ? kv[1] : null;
			value = value == null ? null : valueCodec.decode(value);
			map.put(key, value);
		});
		return map;
	}

	public void appendTo(String target, Map<String, ?> parameterMap) {

	}
}
