package io.basc.framework.sqlite;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		return sql;
	}

	@Override
	public Sql saveOrUpdate(Sql saveSql, Sql updateSql) {
		/**
		 * 保存语句 insert into tableName (columns) values (v1, v2, ...) <br/>
		 * 更新语句 update tableName set a=b where c=d <br/>
		 * 
		 * INSERT OR REPLACE into `test_table1`(`id`,`key`,`value`) select 10,2,11 from
		 * `test_table1` where id=10
		 */
		
		/**
		 * {@link https://stackoverflow.com/questions/418898/sqlite-upsert-not-insert-or-replace/4330694#4330694}
		 * {@link https://stackoverflow.com/questions/2717590/sqlite-insert-on-duplicate-key-update-upsert}
		 */

		List<Sql> insertColumns = SqlUtils.resolveInsertColumns(saveSql);
		if (insertColumns.isEmpty()) {
			// 未显示声明插入字段
			throw new SqlDialectException("Columns to be inserted in the declaration need to be displayed: <"
					+ SqlUtils.toString(saveSql) + ">");
		}

		Set<String> insertColumnSets = insertColumns.stream().map((s) -> SqlUtils.display(s).trim())
				.collect(Collectors.toSet());
		// 过滤insert columns后剩下的字段
		List<SqlExpression> updateColumns = SqlUtils.resolveUpdateSetMap(updateSql).values().stream()
				.filter((s) -> !insertColumnSets.contains(SqlUtils.display(s.getLeft()).trim()))
				.collect(Collectors.toList());

		EditableSql sql = new EditableSql();
		sql.append("INSERT OR REPLACE INTO ");
		sql.append(SqlUtils.resolveInsertTables(saveSql));
		sql.append(" (");
		Iterator<Sql> iterator = insertColumns.iterator();
		while (iterator.hasNext()) {
			sql.append(iterator.next());
			if (iterator.hasNext()) {
				sql.append(", ");
			}
		}

		for (SqlExpression expression : updateColumns) {
			sql.append(", ");
			sql.append(expression.getLeft());
		}
		sql.append(") select ");

		sql.append(SqlUtils.resolveInsertValuesSql(saveSql));

		for (SqlExpression expression : updateColumns) {
			sql.append(",");
			sql.append(expression.getRight());
		}

		sql.append(" from ");
		sql.append(SqlUtils.resolveUpdateTables(updateSql));
		sql.append(WHERE);
		sql.append(SqlUtils.resolveUpdateWhereSql(updateSql));
		return sql;
	}

	@Override
	public <T> Sql toSaveIfAbsentSql(TableStructure tableStructure, T entity) throws SqlDialectException {
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Column> iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.isAutoIncrement()) {
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
}
