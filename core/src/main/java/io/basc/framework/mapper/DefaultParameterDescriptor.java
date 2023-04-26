package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultParameterDescriptor extends AbstractParameterDescriptor {
	private final String name;
	private final TypeDescriptor typeDescriptor;

	@Override
	public ParameterDescriptor rename(String name) {
		return new DefaultParameterDescriptor(name, typeDescriptor);
	}
}
