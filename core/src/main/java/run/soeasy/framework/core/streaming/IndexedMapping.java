package run.soeasy.framework.core.streaming;

import java.util.Collection;
import java.util.function.Supplier;

import run.soeasy.framework.core.domain.KeyValue;

public class IndexedMapping<K, V, W extends Streamable<? extends KeyValue<K, V>>>
		extends CachedStreamable<KeyValue<K, V>, W> implements Mapping<K, V> {
	private static final long serialVersionUID = 1L;

	public IndexedMapping(W source, Supplier<? extends Collection<KeyValue<K, V>>> collectionFactory) {
		super(source, collectionFactory);
	}

	@Override
	public Mapping<K, V> reload() {
		return isReloadable() ? new IndexedMapping<>(source.reload(), collectionFactory) : this;
	}
}
