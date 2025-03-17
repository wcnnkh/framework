package run.soeasy.framework.core.convert.transform.mapping;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.transform.mapping.FieldDescriptor.FieldDescriptorWrapper;
import run.soeasy.framework.core.convert.transform.stereotype.Property;

@Data
public class Field<T extends FieldDescriptor> implements Property, FieldDescriptorWrapper<T> {
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
		// TODO Auto-generated method stub
		return null;
	}
}
