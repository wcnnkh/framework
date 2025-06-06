package run.soeasy.framework.core.type;

import lombok.NonNull;

public interface InstanceFactory {
	boolean canInstantiated(@NonNull ResolvableType requiredType);

	Object newInstance(@NonNull ResolvableType requiredType);
}
