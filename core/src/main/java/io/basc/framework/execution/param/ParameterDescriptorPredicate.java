package io.basc.framework.execution.param;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Assert;

public interface ParameterDescriptorPredicate {
	boolean test(TypeDescriptor sourceTypeDescriptor, ParameterDescriptor parameterDescriptor);

	default ParameterDescriptorPredicate and(ParameterDescriptorPredicate parameterDescriptorMatcher) {
		Assert.requiredArgument(parameterDescriptorMatcher != null, "parameterDescriptorMatcher");
		return (s, p) -> test(s, p) && parameterDescriptorMatcher.test(s, p);
	}

	default ParameterDescriptorPredicate or(ParameterDescriptorPredicate parameterDescriptorMatcher) {
		Assert.requiredArgument(parameterDescriptorMatcher != null, "parameterDescriptorMatcher");
		return (s, p) -> test(s, p) || parameterDescriptorMatcher.test(s, p);
	}
}