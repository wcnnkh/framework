package io.basc.framework.mapper.stereotype;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.transform.Property;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Field implements Property {
	private final FieldDescriptor fieldDescriptor;
	private final Object target;

	@Override
	public String getName() {
		return fieldDescriptor.getName();
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return fieldDescriptor.getter().getTypeDescriptor();
	}

	@Override
	public TypeDescriptor getRequiredTypeDescriptor() {
		return fieldDescriptor.setter().getTypeDescriptor();
	}

	@Override
	public Object getValue() {
		return fieldDescriptor.getter().get(target);
	}

	@Override
	public boolean isReadOnly() {
		return !fieldDescriptor.isSupportSetter();
	}

	@Override
	public void setValue(Object value) {
		fieldDescriptor.setter().set(target, value);
	}
}
