package io.basc.framework.execution.param;

import java.io.Serializable;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Arg implements Parameter, Serializable {
	private static final long serialVersionUID = 1L;
	private int positionIndex;
	private String name;
	private Value valueSource;

	public Arg(int positionIndex, Value valueSource) {
		this(positionIndex, null, valueSource);
	}

	public Arg(String name, Value valueSource) {
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
