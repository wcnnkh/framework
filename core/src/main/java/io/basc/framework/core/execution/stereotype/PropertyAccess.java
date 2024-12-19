package io.basc.framework.core.execution.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Property;
import lombok.Data;
import lombok.NonNull;

@Data
public class PropertyAccess<T extends PropertyAccessDescriptor> implements Property {
	@NonNull
	private final T descriptor;
	private final Object target;

	@Override
	public boolean isWriteable() {
		return descriptor.isWritable();
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return descriptor.getTypeDescriptor();
	}

	@Override
	public boolean isReadable() {
		return descriptor.isReadable();
	}

	@Override
	public void set(Object source) throws UnsupportedOperationException {
		descriptor.getWriteMethod().set(target, source);
	}

	@Override
	public Object get() {
		return descriptor.getReadMethod().get(target);
	}

	@Override
	public String getName() {
		return descriptor.getName();
	}
}
