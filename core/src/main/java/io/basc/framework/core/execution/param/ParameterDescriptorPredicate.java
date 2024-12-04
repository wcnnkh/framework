package io.basc.framework.core.execution.param;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.ParameterDescriptor;
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
