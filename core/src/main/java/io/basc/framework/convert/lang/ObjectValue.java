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
	private Object source;
	private TypeDescriptor typeDescriptor;

	public ObjectValue(Object source) {
		this(source, null);
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
		return new ObjectValue(source, typeDescriptor);
	}

	@Override
	public Object getSource() {
		return source;
	}

	public void setTypeDescriptor(TypeDescriptor typeDescriptor) {
		this.typeDescriptor = typeDescriptor;
	}

	public void setSource(Object source) {
		this.source = source;
	}
}
