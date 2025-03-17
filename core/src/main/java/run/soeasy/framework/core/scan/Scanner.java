package run.soeasy.framework.core.scan;

import lombok.NonNull;
import run.soeasy.framework.util.collections.Elements;

public interface Scanner<T> {
	boolean canScan(String locationPattern);

	default Elements<T> scan(String locationPattern) {
		return scan(locationPattern, null);
	}

	Elements<T> scan(@NonNull String locationPattern, ResourceFilter filter);
}
