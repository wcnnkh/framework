package io.basc.framework.util.page;

import java.util.stream.Stream;

public class AllPage<S extends Pages<K, T>, K, T> extends AllPageable<S, K, T> implements Page<K, T> {

	public AllPage(S source) {
		super(source);
	}

	@Override
	public long getTotal() {
		return source.getTotal();
	}

	@Override
	public long getLimit() {
		Stream<? extends Page<K, T>> stream = source.pages().stream();
		try {
			return stream.mapToLong((e) -> e.getLimit()).sum();
		} finally {
			stream.close();
		}
	}
}
