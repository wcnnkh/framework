package io.basc.framework.core.execution.resolver;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.transform.stereotype.AccessDescriptor;
import lombok.NonNull;

public interface DefaultValueFactory {
	boolean hasDefaultValue(@NonNull AccessDescriptor descriptor);

	Source getDefaultValue(@NonNull AccessDescriptor descriptor);
}
