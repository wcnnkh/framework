package run.soeasy.framework.core.mapping.property;

import lombok.NonNull;
import run.soeasy.framework.core.streaming.Streamable;

public class StreamablePropertyMapping<E extends PropertyDescriptor> implements PropertyMapping<E> {
	private final Streamable<E> elements;

	public StreamablePropertyMapping(@NonNull Streamable<E> elements) {
		this.elements = elements;
	}

	@Override
	public Streamable<E> elements() {
		return elements;
	}

	@Override
	public PropertyMapping<E> reload() {
		return new StreamablePropertyMapping<>(elements.reload());
	}
}
