package scw.util.page;

import java.util.stream.Stream;

@FunctionalInterface
public interface CursorProcessor<K, T> {
	Stream<T> stream(K start, long count);
}
