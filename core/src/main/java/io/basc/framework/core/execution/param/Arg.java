package io.basc.framework.core.execution.param;

import java.io.Serializable;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Any;
import io.basc.framework.core.convert.transform.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Arg implements Parameter, Serializable {
	private static final long serialVersionUID = 1L;
	private int positionIndex;
	private String name;
	private Any valueSource;

	public Arg(int positionIndex, Any valueSource) {
		this(positionIndex, null, valueSource);
	}

	public Arg(String name, Any valueSource) {
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
