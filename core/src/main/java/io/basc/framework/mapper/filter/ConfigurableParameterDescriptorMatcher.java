package io.basc.framework.mapper.filter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.value.ParameterDescriptor;

public class ConfigurableParameterDescriptorMatcher implements ParameterDescriptorMatcher {
	private ParameterDescriptorMatcher parameterDescriptorMatcher;

	public ConfigurableParameterDescriptorMatcher and(ParameterDescriptorMatcher parameterDescriptorMatcher) {
		this.parameterDescriptorMatcher = this.parameterDescriptorMatcher == null ? parameterDescriptorMatcher
				: parameterDescriptorMatcher.and(parameterDescriptorMatcher);
		return this;
	}

	public ConfigurableParameterDescriptorMatcher or(ParameterDescriptorMatcher parameterDescriptorMatcher) {
		this.parameterDescriptorMatcher = this.parameterDescriptorMatcher == null ? parameterDescriptorMatcher
				: parameterDescriptorMatcher.or(parameterDescriptorMatcher);
		return this;
	}

	@Override
	public boolean test(TypeDescriptor sourceTypeDescriptor, ParameterDescriptor parameter) {
		return parameterDescriptorMatcher == null ? true
				: parameterDescriptorMatcher.test(sourceTypeDescriptor, parameter);
	}
}
