package io.basc.framework.mysql;

import io.basc.framework.orm.sql.Column;
import io.basc.framework.orm.sql.PaginationSql;
import io.basc.framework.orm.sql.SqlDialectException;
import io.basc.framework.orm.sql.SqlType;
import io.basc.framework.orm.sql.StandardColumnDescriptor;
import io.basc.framework.orm.sql.StandardSqlDialect;
import io.basc.framework.orm.sql.TableStructure;
import io.basc.framework.orm.sql.TableStructureMapping;
import io.basc.framework.orm.sql.annotation.Counter;
import io.basc.framework.orm.sql.annotation.Table;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.AnyValue;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MysqlDialect extends StandardSqlDialect {
	private static final String DUPLICATE_KEY = " ON DUPLICATE KEY UPDATE ";
	private static final String IF = "IF(";
	private static final String LAST_INSERT_ID_SQL = "select last_insert_id()";

	public SqlType getSqlType(java.lang.Class<?> type) {
		if (ClassUtils.isString(type) || type.isEnum()) {
			return MysqlTypes.VARCHAR;
		} else if (ClassUtils.isBoolean(type)) {
			return MysqlTypes.BIT;
		} else if (ClassUtils.isByte(type)) {
			return MysqlTypes.TINYINT;
		} else if (ClassUtils.isShort(type)) {
			return MysqlTypes.SMALLINT;
		} else if (ClassUtils.isInt(type)) {
			return MysqlTypes.INT;
		} else if (ClassUtils.isLong(type)) {
			return MysqlTypes.BIGINT;
		} else if (ClassUtils.isFloat(type)) {
			return MysqlTypes.FLOAT;
		} else if (ClassUtils.isDouble(type)) {
			return MysqlTypes.DOUBLE;
		} else if (Date.class.isAssignableFrom(type)) {
			return MysqlTypes.DATE;
		} else if (Timestamp.class.isAssignableFrom(type)) {
			return MysqlTypes.TIMESTAMP;
		} else if (Time.class.isAssignableFrom(type)) {
			return MysqlTypes.TIME;
		} else if (Year.class.isAssignableFrom(type)) {
			return MysqlTypes.YEAR;
		} else if (Blob.class.isAssignableFrom(type)) {
			return MysqlTypes.BLOB;
		} else if (Clob.class.isAssignableFrom(type)) {
			return MysqlTypes.BLOB;
		} else if (BigDecimal.class.isAssignableFrom(type)) {
			return MysqlTypes.DECIMAL;
		} else {
			return MysqlTypes.TEXT;
		}
	};

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
			keywordProcessing(cols, column.getName());
			values.append("?");
			params.add(getDataBaseValue(entity, column.getField()));

			if (iterator.hasNext()) {
				cols.append(",");
				values.append(",");
			}
		}

		sb.append(INSERT_INTO_PREFIX);
		keywordProcessing(sb, tableStructure.getName());
		sb.append("(");
		sb.append(cols);
		sb.append(VALUES);
		sb.append(values);
		sb.append(")");
		sb.append(DUPLICATE_KEY);

		iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			keywordProcessing(sb, column.getName());
			sb.append("=");
			appendUpdateValue(sb, params, entity, column, changeMap);
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		return new SimpleSql(sb.toString(), params.toArray());
	}

	@Override
	protected void appendCounterValue(StringBuilder sb, List<Object> params, Object entity, Column column,
			AnyValue oldValue, AnyValue newValue, Counter counter) {
		double change = newValue.getAsDoubleValue() - oldValue.getAsDoubleValue();
		sb.append(IF);
		keywordProcessing(sb, column.getName());
		sb.append("+").append(change);
		sb.append(">=").append(counter.min());
		sb.append(AND);
		keywordProcessing(sb, column.getName());
		sb.append("+").append(change);
		sb.append("<=").append(counter.max());
		sb.append(",");
		keywordProcessing(sb, column.getName());
		sb.append("+").append(change);
		sb.append(",");
		keywordProcessing(sb, column.getName());
		sb.append(")");
	}

	@Override
	public Collection<Sql> createTable(TableStructure tableStructure) {
		StringBuilder sb = new StringBuilder();
		sb.append(getCreateTablePrefix());
		sb.append(" ");
		keywordProcessing(sb, tableStructure.getName());
		sb.append(" (");

		List<Column> primaryKeys = tableStructure.getPrimaryKeys();
		Iterator<Column> iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column col = iterator.next();
			SqlType sqlType = getSqlType(col.getField().getGetter().getType());
			keywordProcessing(sb, col.getName());

			sb.append(" ");
			sb.append(sqlType.getName());
			if (sqlType.getLength() > 0) {
				sb.append("(" + sqlType.getLength() + ")");
			}

			if (primaryKeys.size() == 1) {
				if (col.isPrimaryKey()) {
					sb.append(" PRIMARY KEY");
				}

				if (col.isAutoIncrement()) {
					sb.append(" AUTO_INCREMENT");
				}
			}

			if (col.isUnique()) {
				sb.append(" UNIQUE");
			}

			if (!col.isNullable()) {
				sb.append(" not null");
			}

			String comment = col.getComment();
			if (StringUtils.isNotEmpty(comment)) {
				sb.append(" comment \'").append(comment).append("\'");
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		for (Entry<String, List<Column>> entry : tableStructure.getIndexGroup().entrySet()) {
			sb.append(",");
			sb.append(" INDEX");
			sb.append(" ");
			keywordProcessing(sb, entry.getKey());
			sb.append(" (");
			Iterator<Column> indexIterator = entry.getValue().iterator();
			while (indexIterator.hasNext()) {
				Column column = indexIterator.next();
				keywordProcessing(sb, column.getName());
				if (indexIterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}

		// primary keys
		if (primaryKeys.size() > 1) {
			// 多主键
			sb.append(",primary key(");
			iterator = primaryKeys.iterator();
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

		Table table = tableStructure.getEntityClass().getAnnotation(Table.class);
		if (table != null) {
			if (StringUtils.isNotEmpty(table.engine())) {
				sb.append(" ENGINE=").append(table.engine());
			}

			if (StringUtils.isNotEmpty(table.charset())) {
				sb.append(" CHARSET=").append(table.charset());
			}

			if (StringUtils.isNotEmpty(table.row_format())) {
				sb.append(" ROW_FORMAT=").append(table.row_format());
			}
		}

		if (table != null && !StringUtils.isEmpty(table.comment())) {
			sb.append(" comment=\'").append(table.comment()).append("\'");
		}
		
		return Arrays.asList(new SimpleSql(sb.toString()));
	}

	@Override
	public Sql toLastInsertIdSql(String tableName) throws SqlDialectException {
		return new SimpleSql(LAST_INSERT_ID_SQL);
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
	public TableStructureMapping getTableStructureMapping(Class<?> clazz, String tableName) {
		return new TableStructureMapping() {

			public Sql getSql() {
				return new SimpleSql(
						"select * from INFORMATION_SCHEMA.COLUMNS where table_schema=database() and table_name=?",
						tableName);
			}

			public StandardColumnDescriptor getName(ResultSet resultSet) throws SQLException {
				StandardColumnDescriptor descriptor = new StandardColumnDescriptor();
				descriptor.setName(resultSet.getString("COLUMN_NAME"));
				return descriptor;
			}
		};
	}
}