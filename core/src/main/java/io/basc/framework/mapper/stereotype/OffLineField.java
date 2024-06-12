package io.basc.framework.mapper.stereotype;

public class OffLineField extends AbstractField {
	private Object value;

	public OffLineField(FieldDescriptor fieldDescriptor) {
		super(fieldDescriptor);
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) throws UnsupportedOperationException {
		this.value = value;
	}
}
