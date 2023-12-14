package io.basc.framework.value;

import java.io.Serializable;
import java.util.Objects;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ObjectValue implements Value, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private Object value;
	private TypeDescriptor typeDescriptor;
	private Converter<? super Object, ? super Object, ? extends RuntimeException> converter;

	public ObjectValue(Object value) {
		this(value, null);
	}

	public ObjectValue(Object value, TypeDescriptor typeDescriptor) {
		this(value, typeDescriptor, null);
	}

	@Override
	public ObjectValue clone() {
		return new ObjectValue(value, typeDescriptor, converter);
	}

	@Override
	public boolean equals(Object obj) {
		if (value == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof ObjectValue) {
			return ObjectUtils.equals(value, ((ObjectValue) obj).get());
		}

		return false;
	}

	public Converter<? super Object, ? super Object, ? extends RuntimeException> getConverter() {
		return converter == null ? Value.super.getConverter() : converter;
	}

	@Override
	public Object getSource() {
		return value;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor != null) {
			return typeDescriptor;
		}

		return Value.super.getTypeDescriptor();
	}

	public Object getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return value == null ? super.hashCode() : value.hashCode();
	}

	public void setConverter(ValueConverter converter) {
		this.converter = converter;
	}

	public void setTypeDescriptor(TypeDescriptor typeDescriptor) {
		this.typeDescriptor = typeDescriptor;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return Objects.toString(value);
	}
}
