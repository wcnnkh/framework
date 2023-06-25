package io.basc.framework.net.uri.convert;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MapToQueryString implements
		ReversibleConverter<Map<String, String>, String, ConversionException>, Codec<Map<String, String>, String> {
	private final Codec<String, String> keyCodec;
	private final Codec<String, String> valueCodec;

	@Override
	public String convert(Map<String, String> source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return encode(source);
	}

	@Override
	public Map<String, String> invert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return decode(source);
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

}
