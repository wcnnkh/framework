package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.mapping.Property;
import io.basc.framework.core.mapping.stereotype.FieldDescriptor.FieldDescriptorWrapper;
import lombok.Data;
import lombok.NonNull;

@Data
public class Field<T extends FieldDescriptor> implements Property, FieldDescriptorWrapper<T> {
	@NonNull
	private final T source;
	private final Object target;

	@Override
	public void set(Object value) throws UnsupportedOperationException {
		source.getWriteMethod().set(target, value);
	}

	@Override
	public Object get() {
		return source.getReadMethod().get(target);
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
