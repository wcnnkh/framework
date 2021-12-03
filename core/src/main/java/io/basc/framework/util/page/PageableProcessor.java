package io.basc.framework.util.page;

@FunctionalInterface
public interface PageableProcessor<K, T> {
	Pageable<K, T> process(K cursorId, long count);
}