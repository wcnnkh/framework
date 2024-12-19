package io.basc.framework.core.execution.stractegy;

import io.basc.framework.core.convert.ValueDescriptor;

public interface ValueDescriptorAnalysis {
	boolean isRequired(ValueDescriptor descriptor);
}
