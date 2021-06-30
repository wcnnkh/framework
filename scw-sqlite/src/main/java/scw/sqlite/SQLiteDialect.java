package scw.sqlite;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scw.core.utils.ClassUtils;
import scw.core.utils.NumberUtils;
import scw.core.utils.StringUtils;
import scw.mapper.Field;
import scw.mapper.Fields;
import scw.mysql.MysqlDialect;
import scw.orm.sql.SqlDialectException;
import scw.orm.sql.SqlType;
import scw.orm.sql.TableStructureMapping;
import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.value.AnyValue;

public class SQLiteDialect extends MysqlDialect {
	
	@Override
	public SqlType getSqlType(Class<?> type) {
		if(type == String.class){
			return SQLiteTypes.TEXT;
		}else if(ClassUtils.isFloat(type) || ClassUtils.isDouble(type)){
			return SQLiteTypes.REAL;
		}else if(NumberUtils.isNumber(type)){
			return SQLiteTypes.INTEGER;
		}else if(Blob.class == type){
			return SQLiteTypes.BLOB;
		}else{
			return SQLiteTypes.TEXT;
		}
	}

	@Override
	public Sql toCreateTableSql(String tableName, Class<?> entityClass) throws SqlDialectException {
		StringBuilder sb = new StringBuilder();
		sb.append(getCreateTablePrefix());
		sb.append(" ");
		keywordProcessing(sb, tableName);
		sb.append(" (");

		Fields fields = getFields(entityClass);
		Fields primaryKeys = getPrimaryKeys(entityClass).shared();
		Iterator<Field> iterator = fields.iterator();
		while (iterator.hasNext()) {
			Field col = iterator.next();
			appendFieldName(sb, col.getGetter());
			sb.append(" ");
			scw.orm.sql.SqlType sqlType = getSqlType(col.getGetter().getType());
			sb.append(sqlType.getName());

			if (primaryKeys.size() == 1) {
				if (isPrimaryKey(col)) {
					sb.append(" PRIMARY KEY");
				}

				if (isAutoIncrement(col)) {
					sb.append(" AUTOINCREMENT");
				}
			}

			if (isUnique(col)) {
				sb.append(" UNIQUE");
			}

			if (!isNullable(col)) {
				sb.append(" not null");
			}

			String charsetName = getCharsetName(col.getGetter());
			if (StringUtils.isNotEmpty(charsetName)) {
				sb.append(" character set ").append(charsetName);
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		// primary keys
		if (primaryKeys.size() > 1) {
			// 多主键
			sb.append(",primary key(");
			iterator = primaryKeys.iterator();
			while (iterator.hasNext()) {
				Field column = iterator.next();
				appendFieldName(sb, column.getGetter());
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}

		sb.append(")");
		return new SimpleSql(sb.toString());
	}

	@Override
	public Sql toLastInsertIdSql(String tableName) throws SqlDialectException {
		return new SimpleSql("SELECT last_insert_rowid()");
	}

	@Override
	public <T> Sql toSaveOrUpdateSql(String tableName, Class<? extends T> entityClass, T entity)
			throws SqlDialectException {
		Fields primaryKeys = getPrimaryKeys(entityClass);
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Field> iterator = getFields(entityClass).iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			Object value = column.getGetter().get(entity);
			if (isAutoIncrement(column)) {
				AnyValue anyValue = new AnyValue(value);
				if (value == null || anyValue.isEmpty() || (anyValue.isNumber() && anyValue.getAsInteger() == 0)) {
					continue;
				}
			}

			appendFieldName(cols, column.getGetter());
			values.append("?");
			params.add(value);

			if (iterator.hasNext()) {
				cols.append(",");
				values.append(",");
			}
		}

		sb.append("replace into ");
		keywordProcessing(sb, tableName);
		sb.append("(");
		sb.append(cols);
		sb.append(VALUES);
		sb.append(values);
		sb.append(")");
		return new SimpleSql(sb.toString(), params.toArray());
	}

	@Override
	public TableStructureMapping getTableStructureMapping(Class<?> clazz, final String tableName) {
		return new TableStructureMapping() {

			public Sql getSql() {
				return new SimpleSql("pragma table_info(" + tableName + ")");
			}

			public String getName(ResultSet resultSet) throws SQLException {
				return resultSet.getString("name");
			}
		};
	}
}
