package io.basc.framework.mysql;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.sql.EditableSql;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.orm.Column;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.SqlDialectException;
import io.basc.framework.sql.orm.SqlType;
import io.basc.framework.sql.orm.TableStructure;
import io.basc.framework.sql.orm.TableStructureMapping;
import io.basc.framework.sql.orm.support.StandardColumnMetdata;
import io.basc.framework.sql.orm.support.StandardSqlDialect;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;

public class MysqlDialect extends StandardSqlDialect {
	private static final String DUPLICATE_KEY = " ON DUPLICATE KEY UPDATE ";
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
			if (Timestamp.class.isAssignableFrom(type)) {
				return MysqlTypes.TIMESTAMP;
			} else if (Time.class.isAssignableFrom(type)) {
				return MysqlTypes.TIME;
			}
			return MysqlTypes.DATE;
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
	public Sql toSaveSql(TableStructure tableStructure, Object entity) throws SqlDialectException {
		List<Column> primaryKeys = tableStructure.getPrimaryKeys();
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Column> iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.isAutoIncrement() && !MapperUtils.isExistValue(column.getField(), entity)) {
				continue;
			}

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
		sb.append(")");
		sb.append(VALUES);
		sb.append("(");
		sb.append(values);
		sb.append(")");
		sb.append(DUPLICATE_KEY);

		iterator = tableStructure.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.isAutoIncrement() && !MapperUtils.isExistValue(column.getField(), entity)) {
				continue;
			}

			keywordProcessing(sb, column.getName());
			sb.append("=?");
			params.add(getDataBaseValue(entity, column.getField()));
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		return new SimpleSql(sb.toString(), params.toArray());
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

		for (Entry<IndexInfo, List<Column>> entry : tableStructure.getIndexGroups().entrySet()) {
			sb.append(",");
			sb.append(" INDEX");
			sb.append(" ");
			keywordProcessing(sb, entry.getKey().getName());
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

		if (StringUtils.hasText(tableStructure.getEngine())) {
			sb.append(" ENGINE=").append(tableStructure.getEngine());
		}

		if (StringUtils.hasText(tableStructure.getCharsetName())) {
			sb.append(" CHARSET=").append(tableStructure.getCharsetName());
		}

		if (StringUtils.hasText(tableStructure.getRowFormat())) {
			sb.append(" ROW_FORMAT=").append(tableStructure.getRowFormat());
		}

		if (StringUtils.hasText(tableStructure.getComment())) {
			sb.append(" comment=\'").append(tableStructure.getComment()).append("\'");
		}

		return Arrays.asList(new SimpleSql(sb.toString()));
	}

	@Override
	public Sql toLastInsertIdSql(TableStructure tableStructure) throws SqlDialectException {
		return new SimpleSql(LAST_INSERT_ID_SQL);
	}

	@Override
	public Sql toLimitSql(Sql sql, long start, long limit) throws SqlDialectException {
		StringBuilder sb = new StringBuilder(sql.getSql());
		sb.append(" limit ").append(start).append(",").append(limit);
		return new SimpleSql(sb.toString(), sql.getParams());
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
	public TableStructureMapping getTableStructureMapping(TableStructure tableStructure) {
		return new TableStructureMapping() {

			public Sql getSql() {
				return new SimpleSql(
						"select * from INFORMATION_SCHEMA.COLUMNS where table_schema=database() and table_name=?",
						tableStructure.getName());
			}

			public StandardColumnMetdata getName(ResultSet resultSet) throws SQLException {
				StandardColumnMetdata descriptor = new StandardColumnMetdata();
				descriptor.setName(resultSet.getString("COLUMN_NAME"));
				return descriptor;
			}
		};
	}

	@Override
	public Sql condition(Sql condition, Sql left, Sql right) {
		EditableSql sql = new EditableSql();
		sql.append("IF(");
		sql.append(condition);
		sql.append(",");
		sql.append(left);
		sql.append(",");
		sql.append(right);
		sql.append(")");
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
		sql.append("insert ignore into ");
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
