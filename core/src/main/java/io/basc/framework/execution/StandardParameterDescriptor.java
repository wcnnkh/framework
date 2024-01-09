package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.value.ParameterDescriptor;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StandardParameterDescriptor implements ParameterDescriptor {
	private final String name;
	private final TypeDescriptor typeDescriptor;

	@Override
	public ParameterDescriptor rename(String name) {
		return new StandardParameterDescriptor(name, this.typeDescriptor);
	}
}
