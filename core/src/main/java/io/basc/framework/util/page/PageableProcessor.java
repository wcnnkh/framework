package io.basc.framework.util.page;

@FunctionalInterface
public interface PageableProcessor<K, T> {
	Pageable<K, T> process(K start, long count);
}
