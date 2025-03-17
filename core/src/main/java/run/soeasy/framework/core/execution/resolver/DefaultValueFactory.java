package run.soeasy.framework.core.execution.resolver;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.transform.stereotype.AccessDescriptor;

public interface DefaultValueFactory {
	boolean hasDefaultValue(@NonNull AccessDescriptor descriptor);

	Source getDefaultValue(@NonNull AccessDescriptor descriptor);
}
