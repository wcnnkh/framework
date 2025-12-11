package run.soeasy.framework.core.page;

import java.util.Collection;
import java.util.function.Supplier;

import lombok.NonNull;
import run.soeasy.framework.core.streaming.StreamableWrapper;

@FunctionalInterface
public interface SliceWrapper<K, V, W extends Slice<K, V>> extends Slice<K, V>, StreamableWrapper<V, W> {
	@Override
	default K getCurrentCursor() {
		return getSource().getCurrentCursor();
	}

	@Override
	default K getNextCursor() {
		return getSource().getNextCursor();
	}

	@Override
	default boolean hasNext() {
		return getSource().hasNext();
	}

	@Override
	default boolean isTotalCountKnown() {
		return getSource().isTotalCountKnown();
	}

	@Override
	default Long getTotalCount() {
		return getSource().getTotalCount();
	}

	@Override
	default Slice<K, V> reload() {
		return getSource().reload();
	}

	@Override
	default Slice<K, V> cached() {
		return getSource().cached();
	}

	@Override
	default Slice<K, V> cached(@NonNull Supplier<? extends Collection<V>> collectionFactory) {
		return getSource().cached(collectionFactory);
	}

}
