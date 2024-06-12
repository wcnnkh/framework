package io.basc.framework.execution.param;

import io.basc.framework.convert.TypeDescriptor;

public class ConfigurableParameterDescriptorPredicate implements ParameterDescriptorPredicate {
	private ParameterDescriptorPredicate parameterDescriptorMatcher;

	public ConfigurableParameterDescriptorPredicate and(ParameterDescriptorPredicate parameterDescriptorMatcher) {
		this.parameterDescriptorMatcher = this.parameterDescriptorMatcher == null ? parameterDescriptorMatcher
				: parameterDescriptorMatcher.and(parameterDescriptorMatcher);
		return this;
	}

	public ConfigurableParameterDescriptorPredicate or(ParameterDescriptorPredicate parameterDescriptorMatcher) {
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
