package io.basc.framework.db;

import io.basc.framework.lang.Nullable;

public interface DataBaseResolver {
	@Nullable
	Database resolve(String driverClassName, String url, String username, String password);
}
