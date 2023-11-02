package io.basc.framework.sqlite;

import java.sql.Blob;
import java.util.Iterator;

import io.basc.framework.data.repository.InsertOperationSymbol;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.jdbc.ConnectionOperations;
import io.basc.framework.jdbc.SimpleSql;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.jdbc.template.Column;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.DatabaseURL;
import io.basc.framework.jdbc.template.SqlDialectException;
import io.basc.framework.jdbc.template.SqlType;
import io.basc.framework.jdbc.template.TableMapping;
import io.basc.framework.jdbc.template.support.AbstractSqlDialect;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.NumberUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;

public class SQLiteDialect extends AbstractSqlDialect implements DatabaseDialect {
	private static Logger logger = LoggerFactory.getLogger(SQLiteDialect.class);

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
	public Elements<Sql> toCreateTableSql(TableMapping<?> tableMapping, String tableName) throws SqlDialectException {
		Elements<? extends Column> primaryKeys = tableMapping.getPrimaryKeys();
		StringBuilder sb = new StringBuilder();
		sb.append(getCreateTablePrefix());
		sb.append(" ");
		keywordProcessing(sb, tableName);
		sb.append(" (");

		Iterator<? extends Column> iterator = tableMapping.columns().iterator();
		while (iterator.hasNext()) {
			Column col = iterator.next();
			keywordProcessing(sb, col.getName());
			sb.append(" ");
			io.basc.framework.jdbc.template.SqlType sqlType = getSqlType(
					col.getGetters().first().getTypeDescriptor().getType());
			sb.append(sqlType.getName());
			if (sqlType.getLength() > 0) {
				sb.append("(" + sqlType.getLength() + ")");
			}

			if (primaryKeys.count() == 1) {
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
		return Elements.singleton(new SimpleSql(sb.toString()));
	}

	@Override
	public Sql toLastInsertIdSql(Repository repository) throws SqlDialectException {
		return new SimpleSql("SELECT last_insert_rowid()");
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
	public void concat(StringBuilder sb, String... strs) {
		if (strs == null || strs.length == 0) {
			return;
		}

		for (int i = 0; i < strs.length; i++) {
			if (i != 0) {
				sb.append("||");
			}
			sb.append(strs[i]);
		}
	}

	@Override
	protected String getInsertPrefix(InsertOperationSymbol operationSymbol) {
		if (operationSymbol.getName().equals(InsertOperationSymbol.SAVE_IF_ABSENT.getName())) {
			return "insert or ignore into";
		} else if (operationSymbol.getName().equals(InsertOperationSymbol.SAVE_OR_UPDATE.getName())) {
			return "replace into";
		}
		return super.getInsertPrefix(operationSymbol);
	}

	@Override
	public Elements<String> getTableNames(ConnectionOperations operations) {
		return operations.prepare("select name from sqlite_master where type='table' order by name").query()
				.rows((e) -> e.getString(1));
	}

	@Override
	public Elements<String> getDatabaseNames(ConnectionOperations operations) {
		logger.trace("不支持使用sql获取当前数据库名称");
		return Elements.empty();
	}

	@Override
	public String getSelectedDatabaseName(ConnectionOperations operations) {
		logger.trace("不支持使用sql获取当前连接表名称");
		return null;
	}

	@Override
	public void createDatabase(ConnectionOperations operations, String databaseName) {
		logger.trace("不支持使用sql创建数据库");
	}

	@Override
	public DatabaseURL resolveUrl(String url) {
		return new SQLiteURL(url);
	}
}
