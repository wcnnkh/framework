package io.basc.framework.mapper.stereotype;

public class Field extends AbstractField {
	private final Object target;

	public Field(FieldDescriptor fieldDescriptor, Object target) {
		super(fieldDescriptor);
		this.target = target;
	}

	@Override
	public Object getValue() {
		return getFieldDescriptor().getter().get(target);
	}

	@Override
	public void setValue(Object value) {
		getFieldDescriptor().setter().set(target, value);
	}
}
