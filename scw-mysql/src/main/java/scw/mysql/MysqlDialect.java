package scw.mysql;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.mapper.Field;
import scw.mapper.Fields;
import scw.orm.sql.IndexInfo;
import scw.orm.sql.PaginationSql;
import scw.orm.sql.SqlDialectException;
import scw.orm.sql.SqlType;
import scw.orm.sql.StandardSqlDialect;
import scw.orm.sql.TableStructureMapping;
import scw.orm.sql.annotation.Counter;
import scw.orm.sql.annotation.IndexMethod;
import scw.orm.sql.annotation.IndexOrder;
import scw.orm.sql.annotation.Table;
import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.value.AnyValue;

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
	public <T> Sql toSaveOrUpdateSql(String tableName, Class<? extends T> entityClass, T entity)
			throws SqlDialectException {
		Fields primaryKeys = getPrimaryKeys(entityClass);
		if (primaryKeys.getCount() == 0) {
			throw new NullPointerException("not found primary key");
		}
		
		Map<String, Object> changeMap = getChangeMap(entity);
		StringBuilder sb = new StringBuilder(512);
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Fields fields = getFields(entityClass).shared();
		Iterator<Field> iterator = fields.iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			appendFieldName(cols, column.getGetter());
			values.append("?");
			params.add(getDataBaseValue(entity, column));

			if (iterator.hasNext()) {
				cols.append(",");
				values.append(",");
			}
		}

		sb.append(INSERT_INTO_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append("(");
		sb.append(cols);
		sb.append(VALUES);
		sb.append(values);
		sb.append(")");
		sb.append(DUPLICATE_KEY);
		
		iterator = fields.iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			appendFieldName(sb, column.getGetter());
			sb.append("=");
			AnyValue newValue = new AnyValue(getDataBaseValue(entity, column));
			AnyValue oldValue = new AnyValue(changeMap == null? null:changeMap.get(column.getSetter().getName()));
			appendUpdateValue(sb, params, entity, column, oldValue, newValue);
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		return new SimpleSql(sb.toString(), params.toArray());
	}
	
	@Override
	protected void appendCounterValue(StringBuilder sb, List<Object> params, Object entity, Field column,
			AnyValue oldValue, AnyValue newValue, Counter counter) {
		double change = newValue.getAsDoubleValue() - oldValue.getAsDoubleValue();
		sb.append(IF);
		appendFieldName(sb, column.getGetter());
		sb.append("+").append(change);
		sb.append(">=").append(counter.min());
		sb.append(AND);
		appendFieldName(sb, column.getGetter());
		sb.append("+").append(change);
		sb.append("<=").append(counter.max());
		sb.append(",");
		appendFieldName(sb, column.getGetter());
		sb.append("+").append(change);
		sb.append(",");
		appendFieldName(sb, column.getGetter());
		sb.append(")");
	}
	
	@Override
	public Sql toCreateTableSql(String tableName, Class<?> entityClass) throws SqlDialectException {
		StringBuilder sb = new StringBuilder();
		sb.append(getCreateTablePrefix());
		sb.append(" ");
		keywordProcessing(sb, tableName);
		sb.append(" (");

		Fields primaryKeys = getPrimaryKeys(entityClass);
		Fields columns = getFields(entityClass).distinct();
		Iterator<Field> iterator = columns.iterator();
		while (iterator.hasNext()) {
			Field col = iterator.next();
			SqlType sqlType = getSqlType(col.getGetter().getType());
			appendFieldName(sb, col.getGetter());

			sb.append(" ");
			sb.append(sqlType.getName());
			if (sqlType.getLength() > 0) {
				sb.append("(" + sqlType.getLength() + ")");
			}

			if (primaryKeys.getCount() == 1) {
				if (isPrimaryKey(col)) {
					sb.append(" PRIMARY KEY");
				}

				if (isAutoIncrement(col.getGetter())) {
					sb.append(" AUTO_INCREMENT");
				}
			}

			if (isUnique(col.getGetter()) || isUnique(col.getSetter())) {
				sb.append(" UNIQUE");
			}

			if (!isNullable(col)) {
				sb.append(" not null");
			}

			String comment = getComment(col);
			if (StringUtils.isNotEmpty(comment)) {

				sb.append(" comment \'").append(comment).append("\'");
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		for (Entry<IndexInfo, List<IndexInfo>> entry : getIndexInfoMap(entityClass).entrySet()) {
			sb.append(",");
			if (entry.getKey().getMethod() != IndexMethod.DEFAULT) {
				sb.append(" ");
				sb.append(entry.getKey().getMethod().name());
			}

			sb.append(" INDEX");

			if (!StringUtils.isEmpty(entry.getKey().getName())) {
				sb.append(" ");
				keywordProcessing(sb, entry.getKey().getName());
			}

			sb.append(" (");
			Iterator<IndexInfo> indexIterator = entry.getValue().iterator();
			while (indexIterator.hasNext()) {
				IndexInfo indexInfo = indexIterator.next();
				appendFieldName(sb, indexInfo.getColumn().getGetter());
				if (indexInfo.getLength() != -1) {
					sb.append("(");
					sb.append(indexInfo.getLength());
					sb.append(")");
				}

				if (indexInfo.getOrder() != IndexOrder.DEFAULT) {
					sb.append(" ");
					appendFieldName(sb, indexInfo.getColumn().getGetter());
				}

				if (indexIterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}

		// primary keys
		if (primaryKeys.getCount() > 1) {
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

		Table table = entityClass.getAnnotation(Table.class);
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
		return new SimpleSql(sb.toString());
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

			public String getName(ResultSet resultSet) throws SQLException {
				return resultSet.getString("COLUMN_NAME");
			}
		};
	}
}
