package scw.db;

public class DatabaseConfig extends ConnectionPoolConfig {
	private String cachePrefix;
	private boolean createDatabase = true;
	private boolean checkTableChange = true;
	private boolean tableRegisterManager = true;
	private String tablePackage;

	public String getCachePrefix() {
		return cachePrefix;
	}

	public void setCachePrefix(String cachePrefix) {
		this.cachePrefix = cachePrefix;
	}

	public boolean isCreateDatabase() {
		return createDatabase;
	}

	public void setCreateDatabase(boolean createDatabase) {
		this.createDatabase = createDatabase;
	}

	public boolean isCheckTableChange() {
		return checkTableChange;
	}

	public void setCheckTableChange(boolean checkTableChange) {
		this.checkTableChange = checkTableChange;
	}

	public boolean isTableRegisterManager() {
		return tableRegisterManager;
	}

	public void setTableRegisterManager(boolean tableRegisterManager) {
		this.tableRegisterManager = tableRegisterManager;
	}

	public String getTablePackage() {
		return tablePackage;
	}

	public void setTablePackage(String tablePackage) {
		this.tablePackage = tablePackage;
	}
}
