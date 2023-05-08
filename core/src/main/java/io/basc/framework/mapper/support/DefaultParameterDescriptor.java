package io.basc.framework.mapper.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DefaultParameterDescriptor implements ParameterDescriptor {
	private final String name;
	private final TypeDescriptor typeDescriptor;

	@Override
	public ParameterDescriptor rename(String name) {
		return new DefaultParameterDescriptor(name, typeDescriptor);
	}
}
