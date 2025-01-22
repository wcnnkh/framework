package io.basc.framework.core.execution.resolver;

import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.stereotype.AccessDescriptor;
import lombok.NonNull;

public interface DefaultValueFactory {
	boolean hasDefaultValue(@NonNull AccessDescriptor descriptor);

	Value getDefaultValue(@NonNull AccessDescriptor descriptor);
}
