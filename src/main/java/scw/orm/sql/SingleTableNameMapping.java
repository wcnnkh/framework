package scw.orm.sql;

public class SingleTableNameMapping implements TableNameMapping {
	private Class<?> clazz;
	private String tableName;
	private TableNameMapping tableNameMapping;

	public SingleTableNameMapping(Class<?> clazz, String tableName, TableNameMapping tableNameMapping) {
		this.clazz = clazz;
		this.tableName = tableName;
		this.tableNameMapping = tableNameMapping;
	}

	public String getTableName(Class<?> clazz) {
		if (this.clazz == clazz) {
			return tableName;
		}

		return tableNameMapping.getTableName(clazz);
	}
}
