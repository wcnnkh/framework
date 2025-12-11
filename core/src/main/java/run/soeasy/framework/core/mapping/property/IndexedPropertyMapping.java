package run.soeasy.framework.core.mapping.property;

import java.util.Collection;
import java.util.function.Supplier;

import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.streaming.IndexedMapping;
import run.soeasy.framework.core.streaming.Streamable;

public class IndexedPropertyMapping<E extends PropertyDescriptor, W extends PropertyMapping<E>>
		extends IndexedMapping<String, E, W> implements PropertyMapping<E> {
	private static final long serialVersionUID = 1L;

	public IndexedPropertyMapping(W source, Supplier<? extends Collection<KeyValue<String, E>>> collectionFactory) {
		super(source, collectionFactory);
	}

	@Override
	public Streamable<E> elements() {
		return source.elements();
	}

	@Override
	public PropertyMapping<E> reload() {
		return isReloadable() ? new IndexedPropertyMapping<>(source, collectionFactory) : this;
	}
}
