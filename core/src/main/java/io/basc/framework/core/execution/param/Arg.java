package io.basc.framework.core.execution.param;

import java.io.Serializable;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.ValueWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Arg implements Parameter, Serializable {
	private static final long serialVersionUID = 1L;
	private int positionIndex;
	private String name;
	private ValueWrapper valueSource;

	public Arg(int positionIndex, ValueWrapper valueSource) {
		this(positionIndex, null, valueSource);
	}

	public Arg(String name, ValueWrapper valueSource) {
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
