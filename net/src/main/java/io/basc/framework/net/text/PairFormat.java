package io.basc.framework.net.text;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.StringUtils;

/**
 * 键值对格式化
 * 
 * @param <K>
 * @param <V>
 */
public interface PairFormat<K, V> extends Format<Stream<KeyValue<K, V>>> {

	default void formatMap(Map<? extends K, ? extends V> sourceMap, Appendable target) throws IOException {
		if (CollectionUtils.isEmpty(sourceMap)) {
			return;
		}

		format(sourceMap.entrySet().stream().map((e) -> KeyValue.of(e.getKey(), e.getValue())), target);
	}

	default String formatMap(Map<? extends K, ? extends V> sourceMap) {
		StringBuilder sb = new StringBuilder();
		try {
			formatMap(sourceMap, sb);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
		return sb.toString();
	}

	default void formatMultiValueMap(Map<? extends K, ? extends Collection<? extends V>> sourceMap, Appendable target)
			throws IOException {
		if (CollectionUtils.isEmpty(sourceMap)) {
			return;
		}

		Stream<KeyValue<K, V>> stream = sourceMap.entrySet().stream()
				.flatMap((e) -> e.getValue().stream().map((v) -> KeyValue.of(e.getKey(), v)));
		format(stream, target);
	}

	default String formatMultiValueMap(Map<? extends K, ? extends Collection<? extends V>> sourceMap) {
		StringBuilder sb = new StringBuilder();
		try {
			formatMultiValueMap(sourceMap, sb);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
		return sb.toString();
	}

	default Map<K, V> parseMap(Readable source) throws IOException {
		Stream<KeyValue<K, V>> stream = parse(source);
		return stream.collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
	}

	default Map<K, V> parseMap(String source) {
		if (StringUtils.isEmpty(source)) {
			return Collections.emptyMap();
		}

		StringReader reader = new StringReader(source);
		try {
			return parseMap(reader);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		} finally {
			reader.close();
		}
	}

	default MultiValueMap<K, V> parseMultiValueMap(Readable source) throws IOException {
		Stream<KeyValue<K, V>> stream = parse(source);
		MultiValueMap<K, V> map = new LinkedMultiValueMap<K, V>();
		stream.forEach((e) -> map.add(e.getKey(), e.getValue()));
		return map;
	}

	default MultiValueMap<K, V> parseMultiValueMap(String source) {
		StringReader reader = new StringReader(source);
		try {
			return parseMultiValueMap(reader);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		} finally {
			reader.close();
		}
	}
}
