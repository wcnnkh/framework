package io.basc.framework.util.page;

import java.util.stream.Stream;

public class AllPage<S extends Pageable<K, T>, K, T> extends AllCursor<S, K, T> implements Page<K, T> {

	public AllPage(S source) {
		super(source);
	}

	@Override
	public long getTotal() {
		return source.getTotal();
	}

	@Override
	public long getPageSize() {
		Stream<? extends Page<K, T>> stream = source.pages().stream();
		try {
			return stream.mapToLong((e) -> e.getPageSize()).sum();
		} finally {
			stream.close();
		}
	}
}
