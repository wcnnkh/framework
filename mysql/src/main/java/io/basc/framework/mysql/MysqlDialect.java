package io.basc.framework.mysql;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Year;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import io.basc.framework.data.repository.Repository;
import io.basc.framework.mapper.Getter;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.template.Column;
import io.basc.framework.sql.template.IndexInfo;
import io.basc.framework.sql.template.SqlDialectException;
import io.basc.framework.sql.template.TableMapping;
import io.basc.framework.sql.template.dialect.SqlType;
import io.basc.framework.sql.template.dialect.StandardSqlDialect;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

public class MysqlDialect extends StandardSqlDialect {

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
	public Elements<Sql> toCreateTableSql(TableMapping<?> tableMapping, String tableName) throws SqlDialectException {
		StringBuilder sb = new StringBuilder();
		sb.append(getCreateTablePrefix());
		sb.append(" ");
		keywordProcessing(sb, tableName);
		sb.append(" (");

		Elements<? extends Column> primaryKeys = tableMapping.getPrimaryKeys();
		Iterator<? extends Column> iterator = tableMapping.columns().iterator();
		while (iterator.hasNext()) {
			Column col = iterator.next();
			Getter getter = col.getGetters().first();
			SqlType sqlType = getSqlType(getter.getTypeDescriptor().getType());
			keywordProcessing(sb, col.getName());

			sb.append(" ");
			sb.append(sqlType.getName());
			if (sqlType.getLength() > 0) {
				sb.append("(" + sqlType.getLength() + ")");
			}

			if (primaryKeys.count() == 1) {
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

		for (Entry<IndexInfo, List<Column>> entry : tableMapping.getIndexGroups().entrySet()) {
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
		if (primaryKeys.count() > 1) {
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

		if (StringUtils.hasText(tableMapping.getEngine())) {
			sb.append(" ENGINE=").append(tableMapping.getEngine());
		}

		if (StringUtils.hasText(tableMapping.getCharsetName())) {
			sb.append(" CHARSET=").append(tableMapping.getCharsetName());
		}

		if (StringUtils.hasText(tableMapping.getRowFormat())) {
			sb.append(" ROW_FORMAT=").append(tableMapping.getRowFormat());
		}

		if (StringUtils.hasText(tableMapping.getComment())) {
			sb.append(" comment=\'").append(tableMapping.getComment()).append("\'");
		}
		return Elements.singleton(new SimpleSql(sb.toString()));
	}

	@Override
	public Sql toCopyTableStructureSql(TableMapping<?> tableMapping, String newTableName, String oldTableName)
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
	public Sql toLastInsertIdSql(Repository repository) throws SqlDialectException {
		return new SimpleSql("select last_insert_id()");
	}
}
