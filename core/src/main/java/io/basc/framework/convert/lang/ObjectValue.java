package io.basc.framework.convert.lang;

import java.io.Serializable;

import io.basc.framework.convert.TypeDescriptor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ObjectValue implements Value, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private Object value;
	private TypeDescriptor typeDescriptor;

	public ObjectValue(Object value) {
		this(value, null);
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor != null) {
			return typeDescriptor;
		}
		return Value.super.getTypeDescriptor();
	}

	@Override
	public ObjectValue clone() {
		return new ObjectValue(value, typeDescriptor);
	}

	@Override
	public Object getValue() {
		return value;
	}

	public void setTypeDescriptor(TypeDescriptor typeDescriptor) {
		this.typeDescriptor = typeDescriptor;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
