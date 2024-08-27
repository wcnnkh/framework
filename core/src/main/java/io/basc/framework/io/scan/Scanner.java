package io.basc.framework.io.scan;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;

public interface Scanner<T> {
	boolean canScan(String locationPattern);

	default Elements<T> scan(String locationPattern) {
		return scan(locationPattern, null);
	}

	Elements<T> scan(String locationPattern, @Nullable ResourceFilter filter);
}
