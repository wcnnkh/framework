package io.basc.framework.mapper.filter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParameterNamePrefixFilter extends ParameterDescriptorFilter {
	@NonNull
	private final Elements<String> prefixs;

	@Override
	public boolean test(TypeDescriptor sourceTypeDescriptor, ParameterDescriptor parameter) {
		return prefixs.anyMatch((e) -> parameter.getName().startsWith(e))
				&& super.test(sourceTypeDescriptor, parameter);
	}
}
