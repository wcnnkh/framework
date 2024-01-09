package io.basc.framework.execution;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.element.Indexed;
import io.basc.framework.value.ObjectProperty;
import io.basc.framework.value.ParameterDescriptor;
import io.basc.framework.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Parameter extends ObjectProperty {
	private static final long serialVersionUID = 1L;
	private int index;

	public Parameter(int index, Object value) {
		this(index, value, null);
	}

	public Parameter(int index, Object value, @Nullable TypeDescriptor typeDescriptor) {
		this(index, null, value, typeDescriptor);
	}

	public Parameter(int index, String name, Object value, @Nullable TypeDescriptor typeDescriptor) {
		this(index, name, value, typeDescriptor, null);
	}

	public Parameter(int index, String name, Object value, @Nullable TypeDescriptor typeDescriptor,
			Converter<? super Object, ? super Object, ? extends RuntimeException> converter) {
		super(name, value, typeDescriptor, converter);
		this.index = index;
	}

	public Parameter(int index, String name, Value value) {
		super(name, value);
		this.index = index;
	}

	public Parameter(String name, Object value) {
		this(name, value, null);
	}

	public Parameter(String name, Object value, @Nullable TypeDescriptor typeDescriptor) {
		this(-1, name, value, typeDescriptor);
	}

	@Override
	public Parameter clone() {
		return new Parameter(index, getName(), this);
	}

	@Override
	public Value createRelative(Object value, TypeDescriptor type) {
		return new Parameter(index, getName(), value, type, getConverter());
	}

	@Override
	public boolean isValid() {
		return index > 0 || super.isValid();
	}

	public Parameter rename(String name) {
		return new Parameter(index, name, this);
	}

	public boolean test(Indexed<? extends ParameterDescriptor> indexed) {
		if (getValue() == null && !indexed.getElement().isNullable()) {
			// 不能为空
			return false;
		}

		return (indexed.getIndex() == this.index) || test(indexed.getElement());
	}
}
