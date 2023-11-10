package io.basc.framework.text;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collect.LinkedMultiValueMap;
import io.basc.framework.util.collect.MultiValueMap;

/**
 * 键值对格式化
 * 
 * @param <K>
 * @param <V>
 */
public interface PairFormat<K, V> extends Format<Stream<Pair<K, V>>> {

	default String formatMap(Map<? extends K, ? extends V> sourceMap) {
		return formatMap(sourceMap, new FormatPosition(0));
	}

	default FormatPosition formatMap(Map<? extends K, ? extends V> sourceMap, Appendable target) throws IOException {
		FormatPosition position = new FormatPosition(0);
		formatMap(sourceMap, target, position);
		return position;
	}

	default void formatMap(Map<? extends K, ? extends V> sourceMap, Appendable target, FormatPosition position)
			throws IOException {
		if (CollectionUtils.isEmpty(sourceMap)) {
			return;
		}

		format(sourceMap.entrySet().stream().map((e) -> new Pair<>(e.getKey(), e.getValue())), target, position);
	}

	default String formatMap(Map<? extends K, ? extends V> sourceMap, FormatPosition position) {
		StringBuilder sb = new StringBuilder();
		try {
			formatMap(sourceMap, sb, position);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
		return sb.toString();
	}

	default String formatMultiValueMap(Map<? extends K, ? extends Collection<? extends V>> sourceMap) {
		return formatMultiValueMap(sourceMap, new FormatPosition(0));
	}

	default FormatPosition formatMultiValueMap(Map<? extends K, ? extends Collection<? extends V>> sourceMap,
			Appendable target) throws IOException {
		FormatPosition position = new FormatPosition(0);
		formatMultiValueMap(sourceMap, target, position);
		return position;
	}

	default void formatMultiValueMap(Map<? extends K, ? extends Collection<? extends V>> sourceMap, Appendable target,
			FormatPosition position) throws IOException {
		if (CollectionUtils.isEmpty(sourceMap)) {
			return;
		}

		Stream<Pair<K, V>> stream = sourceMap.entrySet().stream()
				.flatMap((e) -> e.getValue().stream().map((v) -> new Pair<>(e.getKey(), v)));
		format(stream, target, position);
	}

	default String formatMultiValueMap(Map<? extends K, ? extends Collection<? extends V>> sourceMap,
			FormatPosition position) {
		StringBuilder sb = new StringBuilder();
		try {
			formatMultiValueMap(sourceMap, sb, position);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		}
		return sb.toString();
	}

	default Map<K, V> parseMap(Readable source) throws IOException, ParseException {
		ParsePosition pos = new ParsePosition(0);
		Map<K, V> result = parseMap(source, pos);
		if (pos.getIndex() == 0) {
			throw new ParseException("PairFormat.parseMap(Readable) failed", pos.getErrorIndex());
		}
		return result;
	}

	default Map<K, V> parseMap(Readable source, ParsePosition position) throws IOException {
		Stream<Pair<K, V>> stream = parse(source, position);
		return stream.collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
	}

	default Map<K, V> parseMap(String source) throws ParseException {
		ParsePosition pos = new ParsePosition(0);
		Map<K, V> result = parseMap(source, pos);
		if (pos.getIndex() == 0) {
			throw new ParseException("PairFormat.parseMap(String) failed", pos.getErrorIndex());
		}
		return result;
	}

	default Map<K, V> parseMap(String source, ParsePosition position) {
		if (StringUtils.isEmpty(source)) {
			return Collections.emptyMap();
		}

		StringReader reader = new StringReader(source);
		try {
			return parseMap(reader, position);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		} finally {
			reader.close();
		}
	}

	default MultiValueMap<K, V> parseMultiValueMap(Readable source) throws IOException, ParseException {
		ParsePosition pos = new ParsePosition(0);
		MultiValueMap<K, V> result = parseMultiValueMap(source, pos);
		if (pos.getIndex() == 0) {
			throw new ParseException("PairFormat.parseMultiValueMap(Readable) failed", pos.getErrorIndex());
		}
		return result;
	}

	default MultiValueMap<K, V> parseMultiValueMap(Readable source, ParsePosition position) throws IOException {
		Stream<Pair<K, V>> stream = parse(source, position);
		MultiValueMap<K, V> map = new LinkedMultiValueMap<K, V>();
		stream.forEach((e) -> map.add(e.getKey(), e.getValue()));
		return map;
	}

	default MultiValueMap<K, V> parseMultiValueMap(String source) throws ParseException {
		ParsePosition pos = new ParsePosition(0);
		MultiValueMap<K, V> result = parseMultiValueMap(source, pos);
		if (pos.getIndex() == 0) {
			throw new ParseException("PairFormat.parseMultiValueMap(Readable) failed", pos.getErrorIndex());
		}
		return result;
	}

	default MultiValueMap<K, V> parseMultiValueMap(String source, ParsePosition position) {
		StringReader reader = new StringReader(source);
		try {
			return parseMultiValueMap(reader, position);
		} catch (IOException e) {
			throw new IllegalStateException("Should never get here", e);
		} finally {
			reader.close();
		}
	}
}
