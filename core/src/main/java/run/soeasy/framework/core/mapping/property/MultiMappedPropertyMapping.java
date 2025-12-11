package run.soeasy.framework.core.mapping.property;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import run.soeasy.framework.core.streaming.MultiMappedMapping;
import run.soeasy.framework.core.streaming.Streamable;

public class MultiMappedPropertyMapping<E extends PropertyDescriptor, W extends PropertyMapping<E>>
		extends MultiMappedMapping<String, E, W> implements PropertyMapping<E> {

	private static final long serialVersionUID = 1L;

	public MultiMappedPropertyMapping(W source, Supplier<Map<String, Collection<E>>> mapFactory,
			Supplier<Collection<E>> collectionFactory) {
		super(source, mapFactory, collectionFactory);
	}

	@Override
	public PropertyMapping<E> reload() {
		return isReloadable() ? new MultiMappedPropertyMapping<>(source, mapFactory, collectionFactory) : this;
	}

	@Override
	public Streamable<E> elements() {
		return source.elements();
	}
}
