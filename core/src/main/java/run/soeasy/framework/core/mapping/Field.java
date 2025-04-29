package run.soeasy.framework.core.mapping;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.mapping.PropertyAccessor;
import run.soeasy.framework.core.mapping.FieldDescriptor.FieldDescriptorWrapper;

@Data
public class Field<T extends FieldDescriptor> implements PropertyAccessor, FieldDescriptorWrapper<T> {
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

	@Override
	public Field<T> rename(String name) {
		return null;
	}
}
