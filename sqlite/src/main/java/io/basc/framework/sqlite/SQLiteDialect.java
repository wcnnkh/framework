package io.basc.framework.sqlite;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.sql.EditableSql;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.orm.Column;
import io.basc.framework.sql.orm.ColumnDescriptor;
import io.basc.framework.sql.orm.SqlDialectException;
import io.basc.framework.sql.orm.SqlType;
import io.basc.framework.sql.orm.StandardColumnDescriptor;
import io.basc.framework.sql.orm.StandardSqlDialect;
import io.basc.framework.sql.orm.TableStructure;
import io.basc.framework.sql.orm.TableStructureMapping;
import io.basc.framework.sql.orm.annotation.Counter;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.NumberUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.AnyValue;

public class SQLiteDialect extends StandardSqlDialect {

	@Override
	public SqlType getSqlType(Class<?> type) {
		if (type == String.class) {
			return SQLiteTypes.TEXT;
		} else if (ClassUtils.isFloat(type) || ClassUtils.isDouble(type)) {
			return SQLiteTypes.REAL;
		} else if (NumberUtils.isNumber(type)) {
			return SQLiteTypes.INTEGER;
		} else if (Blob.class == type) {
			return SQLiteTypes.BLOB;
		} else {
			return SQLiteTypes.TEXT;
		}
	}

	@Override
	public Collection<Sql> createTable(TableStructure tableStructure) throws SqlDialectException {
		List<Column> primaryKeys = tableStructure.getPrimaryKeys();
		StringBuilder sb = new StringBuilder();
		sb.append(getCreateTablePrefix());
		sb.append(" ");
		keywordProcessing(sb, tableStructure.getName());
		sb.append(" (");

		Iterator<Column> iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column col = iterator.next();
			keywordProcessing(sb, col.getName());
			sb.append(" ");
			io.basc.framework.sql.orm.SqlType sqlType = getSqlType(col.getField().getGetter().getType());
			sb.append(sqlType.getName());
			if (sqlType.getLength() > 0) {
				sb.append("(" + sqlType.getLength() + ")");
			}

			if (primaryKeys.size() == 1) {
				if (col.isPrimaryKey()) {
					sb.append(" PRIMARY KEY");
				}

				if (col.isAutoIncrement()) {
					sb.append(" AUTOINCREMENT");
				}
			}

			if (col.isUnique()) {
				sb.append(" UNIQUE");
			}

			if (!col.isNullable()) {
				sb.append(" not null");
			}

			String charsetName = col.getCharsetName();
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
			iterator = tableStructure.getPrimaryKeys().iterator();
			while (iterator.hasNext()) {
				Column column = iterator.next();
				keywordProcessing(sb, column.getName());
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}

		sb.append(")");
		return Arrays.asList(new SimpleSql(sb.toString()));
	}

	@Override
	public Sql toLastInsertIdSql(TableStructure tableStructure) throws SqlDialectException {
		return new SimpleSql("SELECT last_insert_rowid()");
	}

	@Override
	protected void appendCounterValue(StringBuilder sb, List<Object> params, Object entity, Column column,
			AnyValue oldValue, AnyValue newValue, Counter counter) {
		double change = newValue.getAsDoubleValue() - oldValue.getAsDoubleValue();
		sb.append("CASE WHEN ");
		keywordProcessing(sb, column.getName());
		sb.append("+").append(change);
		sb.append(">=").append(counter.min());
		sb.append(AND);
		keywordProcessing(sb, column.getName());
		sb.append("+").append(change);
		sb.append("<=").append(counter.max());
		sb.append(" THEN ");
		keywordProcessing(sb, column.getName());
		sb.append("+").append(change);
		sb.append(" ELSE ");
		keywordProcessing(sb, column.getName());
		sb.append(")");
	}

	@Override
	public TableStructureMapping getTableStructureMapping(TableStructure tableStructure) {
		return new TableStructureMapping() {

			public Sql getSql() {
				return new SimpleSql("pragma table_info(" + tableStructure.getName() + ")");
			}

			public ColumnDescriptor getName(ResultSet resultSet) throws SQLException {
				StandardColumnDescriptor descriptor = new StandardColumnDescriptor();
				descriptor.setName(resultSet.getString("name"));
				return descriptor;
			}
		};
	}

	@Override
	public Sql toCopyTableStructureSql(Class<?> entityClass, String newTableName, String oldTableName)
			throws SqlDialectException {
		StringBuilder sb = new StringBuilder();
		sb.append(getCreateTablePrefix());
		sb.append(" ");
		keywordProcessing(sb, newTableName);
		sb.append(" like ");
		keywordProcessing(sb, oldTableName);
		return new SimpleSql(sb.toString());
	}

	@Override
	public Sql condition(Sql condition, Sql left, Sql right) {
		EditableSql sql = new EditableSql();
		sql.append("CASE WHEN ");
		sql.append(condition);
		sql.append(" THEN ");
		sql.append(left);
		sql.append(" ELSE ");
		sql.append(right);
		return sql;
	}

	@Override
	public Sql toSaveIfAbsentSql(TableStructure tableStructure, Object entity) throws SqlDialectException {
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Column> iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.isAutoIncrement() && !MapperUtils.isExistValue(column.getField(), entity)) {
				continue;
			}

			if (cols.length() > 0) {
				cols.append(",");
				values.append(",");
			}

			keywordProcessing(cols, column.getName());
			values.append("?");
			params.add(getDataBaseValue(entity, column.getField()));
		}
		sql.append("insert or ignore into ");
		keywordProcessing(sql, tableStructure.getName());
		sql.append("(");
		sql.append(cols);
		sql.append(")");
		sql.append(VALUES);
		sql.append("(");
		sql.append(values);
		sql.append(")");
		return new SimpleSql(sql.toString(), params.toArray());
	}

	@Override
	public Sql toLimitSql(Sql sql, long start, long limit) throws SqlDialectException {
		StringBuilder sb = new StringBuilder(sql.getSql());
		sb.append(" limit ").append(start).append(",").append(limit);
		return new SimpleSql(sb.toString(), sql.getParams());
	}

	@Override
	public Sql toSaveSql(TableStructure tableStructure, Object entity) throws SqlDialectException {
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Column> iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.isAutoIncrement() && !MapperUtils.isExistValue(column.getField(), entity)) {
				continue;
			}

			if (cols.length() > 0) {
				cols.append(",");
				values.append(",");
			}

			keywordProcessing(cols, column.getName());
			values.append("?");
			params.add(getDataBaseValue(entity, column.getField()));
		}
		sql.append("replace into ");
		keywordProcessing(sql, tableStructure.getName());
		sql.append("(");
		sql.append(cols);
		sql.append(")");
		sql.append(VALUES);
		sql.append("(");
		sql.append(values);
		sql.append(")");
		return new SimpleSql(sql.toString(), params.toArray());
	}
}
