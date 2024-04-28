package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.value.ObjectProperty;
import io.basc.framework.value.ParameterDescriptor;
import io.basc.framework.value.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Parameter extends ObjectProperty {
	private static final long serialVersionUID = 1L;

	public Parameter(int index, Object value) {
		this(index, value, null);
	}

	public Parameter(int index, Object value, @Nullable TypeDescriptor typeDescriptor) {
		this(index, null, value, typeDescriptor);
	}

	public Parameter(int index, String name, Object value, @Nullable TypeDescriptor typeDescriptor) {
		super(name, value, typeDescriptor);
	}

	public Parameter(int index, String name, Value value) {
		super(name, value);
		setPositionIndex(index);
	}

	public Parameter(String name, Object value) {
		this(name, value, null);
	}

	public Parameter(String name, Object value, @Nullable TypeDescriptor typeDescriptor) {
		this(-1, name, value, typeDescriptor);
	}

	@Override
	public Parameter clone() {
		return new Parameter(getPositionIndex(), getName(), this);
	}

	@Override
	public boolean isValid() {
		return getPositionIndex() >= 0 || super.isValid();
	}

	public Parameter rename(String name) {
		return new Parameter(getPositionIndex(), name, this);
	}
	
	@Override
	public boolean test(ParameterDescriptor target) {
		if (getValue() == null && !target.isNullable()) {
			// 不能为空
			return false;
		}
		return super.test(target);
	}
}
