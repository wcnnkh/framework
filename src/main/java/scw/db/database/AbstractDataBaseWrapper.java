package scw.db.database;

public abstract class AbstractDataBaseWrapper implements DataBase {
	public abstract DataBase getTargetDataBase();

	public String getConnectionURL() {
		return getTargetDataBase().getConnectionURL();
	}

	public String getDataBase() {
		return getTargetDataBase().getDataBase();
	}

	public String getDriverClassName() {
		return getTargetDataBase().getDriverClassName();
	}

	public String getUsername() {
		return getTargetDataBase().getUsername();
	}

	public String getPassword() {
		return getTargetDataBase().getPassword();
	}

	public DataBaseType getDataBaseType() {
		return getTargetDataBase().getDataBaseType();
	}

	public void create() {
		getTargetDataBase().create();
	}

	public void create(String database) {
		getTargetDataBase().create(database);
	}

}
