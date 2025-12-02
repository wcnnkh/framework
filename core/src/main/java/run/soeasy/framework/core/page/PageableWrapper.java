package run.soeasy.framework.core.page;

import run.soeasy.framework.core.collection.ListableWrapper;

@FunctionalInterface
public interface PageableWrapper<K, V, W extends Pageable<K, V>> extends Pageable<K, V>, ListableWrapper<V, W> {
	@Override
	default K getCurrentCursor() {
		return getSource().getCurrentCursor();
	}

	@Override
	default K getNextCursor() {
		return getSource().getNextCursor();
	}

	@Override
	default boolean hasNextPage() {
		return getSource().hasNextPage();
	}

	@Override
	default boolean isKnownTotal() {
		return getSource().isKnownTotal();
	}

	@Override
	default Long getTotalCount() {
		return getSource().getTotalCount();
	}
}
