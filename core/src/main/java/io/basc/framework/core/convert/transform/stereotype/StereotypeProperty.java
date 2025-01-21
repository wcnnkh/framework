package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Property;
import io.basc.framework.core.convert.transform.stereotype.StereotypeDescriptor.StereotypeDescriptorWrapper;
import lombok.Data;
import lombok.NonNull;

@Data
public class StereotypeProperty<T extends StereotypeDescriptor> implements Property, StereotypeDescriptorWrapper<T> {
	@NonNull
	private final T source;
	private final Object target;

	@Override
	public void set(Object value) throws UnsupportedOperationException {
		source.getWriter().set(target, value);
	}

	@Override
	public Object get() {
		return source.getReader().get(target);
	}

	@Override
	public boolean isReadable() {
		return source.isReadable();
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return source.getTypeDescriptor();
	}
}
