package io.basc.framework.execution.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class SimpleParameter extends SimpleParameterDescriptor implements Parameter {
	private Object value;

	public SimpleParameter(ParameterDescriptor parameterDescriptor) {
		super(parameterDescriptor);
	}

	public SimpleParameter(Parameter parameter) {
		this((ParameterDescriptor) parameter);
		this.value = parameter.getValue();
	}
}
