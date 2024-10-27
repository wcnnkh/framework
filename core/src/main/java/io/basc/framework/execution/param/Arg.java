package io.basc.framework.execution.param;

import java.io.Serializable;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ObjectValue;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Arg implements Parameter, Serializable {
	private static final long serialVersionUID = 1L;
	private int positionIndex;
	private String name;
	private ObjectValue valueSource;

	public Arg(int positionIndex, ObjectValue valueSource) {
		this(positionIndex, null, valueSource);
	}

	public Arg(String name, ObjectValue valueSource) {
		this(-1, name, valueSource);
	}

	@Override
	public Object getValue() {
		return valueSource.getValue();
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return valueSource.getTypeDescriptor();
	}
}
