package io.basc.framework.jdbc.mysql;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Year;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.core.execution.Getter;
import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.jdbc.ConnectionOperations;
import io.basc.framework.jdbc.SimpleSql;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.DatabaseURL;
import io.basc.framework.jdbc.template.SqlDialectException;
import io.basc.framework.jdbc.template.SqlType;
import io.basc.framework.jdbc.template.support.AbstractSqlDialect;
import io.basc.framework.orm.ColumnDescriptor;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collection.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MysqlDialect extends AbstractSqlDialect implements DatabaseDialect {
	private String charsetName = "utf8";
	private String collate = "utf8_general_ci";

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
	public Elements<Sql> toCreateTableSql(EntityMapping<?> tableMapping, String tableName) throws SqlDialectException {
		StringBuilder sb = new StringBuilder();
		sb.append(getCreateTablePrefix());
		sb.append(" ");
		keywordProcessing(sb, tableName);
		sb.append(" (");

		Elements<? extends ColumnDescriptor> primaryKeys = tableMapping.getPrimaryKeys();
		Iterator<? extends ColumnDescriptor> iterator = tableMapping.columns().iterator();
		while (iterator.hasNext()) {
			ColumnDescriptor col = iterator.next();
			Getter getter = col.getter();
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

		Map<IndexInfo, List<ColumnDescriptor>> indexGroupMap = getIndexGroups(tableMapping.columns());
		for (Entry<IndexInfo, List<ColumnDescriptor>> entry : indexGroupMap.entrySet()) {
			sb.append(",");
			sb.append(" INDEX");
			sb.append(" ");
			keywordProcessing(sb, entry.getKey().getName());
			sb.append(" (");
			Iterator<ColumnDescriptor> indexIterator = entry.getValue().iterator();
			while (indexIterator.hasNext()) {
				ColumnDescriptor column = indexIterator.next();
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
				ColumnDescriptor column = iterator.next();
				keywordProcessing(sb, column.getName());
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}
		sb.append(")");

		if (StringUtils.hasText(tableMapping.getCharsetName())) {
			sb.append(" CHARSET=").append(tableMapping.getCharsetName());
		}

		if (StringUtils.hasText(tableMapping.getComment())) {
			sb.append(" comment=\'").append(tableMapping.getComment()).append("\'");
		}
		return Elements.singleton(new SimpleSql(sb.toString()));
	}

	@Override
	public Sql toCopyTableStructureSql(EntityMapping<?> tableMapping, String newTableName, String oldTableName)
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

	@Override
	protected String getInsertPrefix(InsertOperationSymbol operationSymbol) {
		if (operationSymbol.getName().equals(InsertOperationSymbol.SAVE_IF_ABSENT.getName())) {
			return "insert ignore into";
		} else if (operationSymbol.getName().equals(InsertOperationSymbol.SAVE_OR_UPDATE.getName())) {
			return "replace into";
		} else {
			return super.getInsertPrefix(operationSymbol);
		}
	}

	@Override
	public Elements<String> getTableNames(ConnectionOperations operations) {
		return operations.prepare("SHOW TABLES").query().rows((e) -> e.getString(1));
	}

	@Override
	public Elements<String> getDatabaseNames(ConnectionOperations operations) {
		return operations.prepare("SHOW DATABASES").query().rows((e) -> e.getString(1));
	}

	@Override
	public String getSelectedDatabaseName(ConnectionOperations operations) {
		return operations.prepare("SHOW DATABASE()").query().rows((e) -> e.getString(1)).first();
	}

	@Override
	public void createDatabase(ConnectionOperations operations, String databaseName) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE DATABASE IF NOT EXISTS `").append(databaseName).append("`");
		if (!StringUtils.isEmpty(charsetName)) {
			sb.append(" CHARACTER SET ").append(charsetName);
		}

		if (!StringUtils.isEmpty(collate)) {
			sb.append(" COLLATE ").append(collate);
		}

		operations.prepare(sb.toString()).execute();
	}

	@Override
	public DatabaseURL resolveUrl(String url) {
		return new MysqlURL(url);
	}
}
