package io.basc.framework.core.scan;

import io.basc.framework.util.collection.Elements;
import lombok.NonNull;

public interface Scanner<T> {
	boolean canScan(String locationPattern);

	default Elements<T> scan(String locationPattern) {
		return scan(locationPattern, null);
	}

	Elements<T> scan(@NonNull String locationPattern, ResourceFilter filter);
}
