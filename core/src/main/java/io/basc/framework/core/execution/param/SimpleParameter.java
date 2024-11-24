package io.basc.framework.core.execution.param;

import io.basc.framework.core.convert.TypeDescriptor;
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

	@Override
	public TypeDescriptor getTypeDescriptor() {
		TypeDescriptor typeDescriptor = super.getTypeDescriptor();
		if (typeDescriptor == DEFAULT_TYPE_DESCRIPTOR && value != null) {
			return TypeDescriptor.forObject(value);
		}
		return typeDescriptor;
	}
}
