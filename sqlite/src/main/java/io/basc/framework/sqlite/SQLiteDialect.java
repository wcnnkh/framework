package io.basc.framework.sqlite;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.orm.sql.Column;
import io.basc.framework.orm.sql.ColumnDescriptor;
import io.basc.framework.orm.sql.PaginationSql;
import io.basc.framework.orm.sql.SqlDialectException;
import io.basc.framework.orm.sql.SqlType;
import io.basc.framework.orm.sql.StandardColumnDescriptor;
import io.basc.framework.orm.sql.StandardSqlDialect;
import io.basc.framework.orm.sql.TableStructure;
import io.basc.framework.orm.sql.TableStructureMapping;
import io.basc.framework.orm.sql.annotation.Counter;
import io.basc.framework.sql.EditableSql;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlException;
import io.basc.framework.sql.SqlExpression;
import io.basc.framework.sql.SqlUtils;
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
			io.basc.framework.orm.sql.SqlType sqlType = getSqlType(col.getField().getGetter().getType());
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
	public Sql toLastInsertIdSql(String tableName) throws SqlDialectException {
		return new SimpleSql("SELECT last_insert_rowid()");
	}

	@Override
	public <T> Sql toSaveOrUpdateSql(TableStructure tableStructure, T entity) throws SqlDialectException {
		List<Column> primaryKeys = tableStructure.getPrimaryKeys();
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		Map<String, Object> changeMap = getChangeMap(entity);
		StringBuilder sb = new StringBuilder(512);
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Column> iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			Object value = column.getField().getGetter().get(entity);
			if (column.isAutoIncrement()) {
				AnyValue anyValue = new AnyValue(value);
				if (value == null || anyValue.isEmpty() || (anyValue.isNumber() && anyValue.getAsInteger() == 0)) {
					continue;
				}
			}

			keywordProcessing(cols, column.getName());
			if (column.isPrimaryKey()) {
				values.append("?");
				params.add(value);
			} else {
				appendUpdateValue(values, params, entity, column, changeMap);
			}

			if (iterator.hasNext()) {
				cols.append(",");
				values.append(",");
			}
		}

		sb.append("replace into ");
		keywordProcessing(sb, tableStructure.getName());
		sb.append("(");
		sb.append(cols);
		sb.append(")");
		sb.append(VALUES);
		sb.append("(");
		sb.append(values);
		sb.append(")");
		return new SimpleSql(sb.toString(), params.toArray());
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
	public TableStructureMapping getTableStructureMapping(Class<?> clazz, final String tableName) {
		return new TableStructureMapping() {

			public Sql getSql() {
				return new SimpleSql("pragma table_info(" + tableName + ")");
			}

			public ColumnDescriptor getName(ResultSet resultSet) throws SQLException {
				StandardColumnDescriptor descriptor = new StandardColumnDescriptor();
				descriptor.setName(resultSet.getString("name"));
				return descriptor;
			}
		};
	}

	@Override
	public PaginationSql toPaginationSql(Sql sql, long start, long limit) throws SqlDialectException {
		String str = sql.getSql();
		int fromIndex = str.toLowerCase().indexOf(" from ");// ignore select
		if (fromIndex == -1) {
			throw new IndexOutOfBoundsException(str);
		}

		String whereSql;
		int orderIndex = str.toLowerCase().lastIndexOf(" order by ");
		if (orderIndex == -1) {// 不存在 order by 子语句
			whereSql = str.substring(fromIndex);
		} else {
			whereSql = str.substring(fromIndex, orderIndex);
		}

		Sql countSql = new SimpleSql("select count(*)" + whereSql, sql.getParams());
		StringBuilder sb = new StringBuilder(str);
		sb.append(" limit ").append(start).append(",").append(limit);
		return new PaginationSql(countSql, new SimpleSql(sb.toString(), sql.getParams()));
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
		sql.append(")");
		return sql;
	}

	@Override
	public Sql saveOrUpdate(Sql saveSql, Sql updateSql) {
		/**
		 * 保存语句 insert into tableName (columns) values (v1, v2, ...) <br/>
		 * 更新语句 update tableName set a=b where c=d <br/>
		 * 保存或更新语句 replace into tableName (columns) values (v1, v2, ...) <br/>
		 */
		List<String> insertColumns = SqlUtils.resolveInsertColumns(saveSql);
		if (insertColumns.isEmpty()) {
			throw new NotSupportedException("Declaration insertion field to display");
		}

		List<Sql> insertValues = SqlUtils.resolveInsertValues(saveSql);
		if (insertColumns.size() != insertValues.size()) {
			// 列数量和值数量不一致
			throw new SqlException("Declaration insertion field to display");
		}

		String insertSql = saveSql.getSql();
		insertSql = insertSql.toLowerCase();
		int prefixEndIndex = insertSql.indexOf("(");
		if (prefixEndIndex == -1) {
			throw new IllegalArgumentException(SqlUtils.toString(insertSql));
		}

		EditableSql sql = new EditableSql();
		sql.append("replace");
		sql.append(SqlUtils.sub(saveSql, "insert".length(), prefixEndIndex));

		sql.append("(");
		Iterator<String> columnsIterator = insertColumns.iterator();
		while (columnsIterator.hasNext()) {
			sql.append(columnsIterator.next());
			if (columnsIterator.hasNext()) {
				sql.append(",");
			}
		}

		Map<String, SqlExpression> setMap = SqlUtils.resolveUpdateSetMap(updateSql);
		// 过滤掉insert columns中已经存在的字段
		List<SqlExpression> sets = setMap.values().stream().filter((s) -> !insertColumns.contains(s.getName()))
				.collect(Collectors.toList());
		for (SqlExpression expression : sets) {
			sql.append(",");
			sql.append(expression.getLeft());
		}
		sql.append(")");

		columnsIterator = insertColumns.iterator();
		Iterator<Sql> valuesIterator = insertValues.iterator();
		Sql updateWhereSql = SqlUtils.resolveUpdateWhereSql(updateSql);
		sql.append(VALUES);
		sql.append("(");
		while (columnsIterator.hasNext() && valuesIterator.hasNext()) {
			String columnName = columnsIterator.next();
			Sql value = valuesIterator.next();
			Sql condition = condition(updateWhereSql, value, new SimpleSql(columnName));
			sql.append(condition);
			if (columnsIterator.hasNext() && valuesIterator.hasNext()) {
				sql.append(",");
			}
		}

		for (SqlExpression expression : sets) {
			sql.append(",");
			Sql condition = condition(updateWhereSql, expression.getRight(), expression.getRight());
			sql.append(condition);
		}
		sql.append(")");
		return sql;
	}
}
