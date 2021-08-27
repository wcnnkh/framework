package io.basc.framework.util.page;

import java.util.stream.Stream;

@FunctionalInterface
public interface CursorProcessor<K, T> {
	Stream<T> process(K start, long count);
}
