package scw.util.page;

import java.util.List;

@FunctionalInterface
public interface CursorProcessor<K, T> {
	List<T> process(K start, long count);
}
