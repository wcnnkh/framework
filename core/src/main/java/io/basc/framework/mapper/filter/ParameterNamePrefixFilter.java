package io.basc.framework.mapper.filter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.value.ParameterDescriptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ParameterNamePrefixFilter extends ParameterDescriptorFilter {
	private final String prefix;

	public ParameterNamePrefixFilter(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public boolean test(TypeDescriptor sourceTypeDescriptor, ParameterDescriptor parameter) {
		return parameter.getName().startsWith(prefix) && super.test(sourceTypeDescriptor, parameter);
	}
}
