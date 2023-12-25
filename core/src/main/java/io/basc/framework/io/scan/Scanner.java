package io.basc.framework.io.scan;

import io.basc.framework.util.element.Elements;

public interface Scanner<T> {
	boolean canScan(String locationPattern);

	Elements<T> scan(String locationPattern);
}
