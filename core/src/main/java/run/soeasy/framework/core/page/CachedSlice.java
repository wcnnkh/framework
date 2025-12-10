package run.soeasy.framework.core.page;

import java.util.Collection;
import java.util.function.Supplier;

import run.soeasy.framework.core.streaming.CachedStreamable;

public class CachedSlice<K, V, W extends Slice<K, V>> extends CachedStreamable<V, W>
		implements Slice<K, V>, SliceWrapper<K, V, W> {
	private static final long serialVersionUID = 1L;

	public CachedSlice(W source, Supplier<? extends Collection<V>> collectionFactory) {
		super(source, collectionFactory);
	}

	@Override
	public Slice<K, V> reload() {
		return isReloadable() ? new CachedSlice<>(source, collectionFactory) : this;
	}

	@Override
	public W getSource() {
		return source;
	}
}
