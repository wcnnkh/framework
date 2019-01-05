package scw.db.sql;

import java.util.Map;

import scw.database.SQL;
import scw.database.TableInfo;

public interface SQLFormat {
	SQL toCreateTableSql(TableInfo tableInfo, String tableName);

	SQL toInsertSql(Object obj, TableInfo tableInfo, String tableName);

	SQL toUpdateSql(Object obj, TableInfo tableInfo, String tableName);

	SQL toUpdateSql(TableInfo tableInfo, String tableName,
			Map<String, Object> valueMap, Object... params);

	SQL toSaveOrUpdateSql(Object obj, TableInfo tableInfo, String tableName);

	SQL toDeleteSql(Object bean, TableInfo tableInfo, String tableName);

	SQL toDeleteSql(TableInfo tableInfo, String tableName, Object... params);

	SQL toSelectByIdSql(TableInfo tableInfo, String tableName, Object... params);

	SQL toIncrSql(Object obj, TableInfo tableInfo, String tableName,
			String fieldName, double limit, Double maxValue);

	SQL toDecrSql(Object obj, TableInfo tableInfo, String tableName,
			String fieldName, double limit, Double minValue);
}
