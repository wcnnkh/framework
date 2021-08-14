package scw.db;

import scw.lang.Nullable;

public interface DataBaseResolver {
	@Nullable
	DataBase resolve(String driverClassName, String url, String username, String password);
}
