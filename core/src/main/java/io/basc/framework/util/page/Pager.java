package io.basc.framework.util.page;

import io.basc.framework.util.Elements;

@FunctionalInterface
public interface Pager<K, T> {
	Elements<T> paging(K start, long count);
}
