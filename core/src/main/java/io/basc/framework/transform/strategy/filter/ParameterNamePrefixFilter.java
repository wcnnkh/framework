package io.basc.framework.transform.strategy.filter;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.param.ParameterDescriptor;
import io.basc.framework.util.Elements;
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
