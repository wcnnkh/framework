package run.soeasy.framework.core.mapping.property;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import run.soeasy.framework.core.streaming.MappedMapping;
import run.soeasy.framework.core.streaming.Streamable;

public class MappedPropertyMapping<E extends PropertyDescriptor, W extends PropertyMapping<E>>
		extends MappedMapping<String, E, W> implements PropertyMapping<E> {
	private static final long serialVersionUID = 1L;

	public MappedPropertyMapping(W source, BinaryOperator<E> mergeFunction, Supplier<Map<String, E>> mapFactory) {
		super(source, mergeFunction, mapFactory);
	}

	@Override
	public PropertyMapping<E> reload() {
		return isReloadable() ? new MappedPropertyMapping<>(source, mergeFunction, mapFactory) : this;
	}

	@Override
	public Streamable<E> elements() {
		return source.elements();
	}
}
