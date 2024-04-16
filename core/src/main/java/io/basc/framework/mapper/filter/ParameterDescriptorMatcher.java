package io.basc.framework.mapper.filter;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.value.ParameterDescriptor;

public interface ParameterDescriptorMatcher {
	boolean test(TypeDescriptor sourceTypeDescriptor, ParameterDescriptor parameterDescriptor);

	default ParameterDescriptorMatcher and(ParameterDescriptorMatcher parameterDescriptorMatcher) {
		Assert.requiredArgument(parameterDescriptorMatcher != null, "parameterDescriptorMatcher");
		return (s, p) -> test(s, p) && parameterDescriptorMatcher.test(s, p);
	}

	default ParameterDescriptorMatcher or(ParameterDescriptorMatcher parameterDescriptorMatcher) {
		Assert.requiredArgument(parameterDescriptorMatcher != null, "parameterDescriptorMatcher");
		return (s, p) -> test(s, p) || parameterDescriptorMatcher.test(s, p);
	}
}
