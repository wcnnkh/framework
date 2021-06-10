package scw.mysql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import scw.convert.TypeDescriptor;
import scw.core.utils.StringUtils;
import scw.lang.ParameterException;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mapper.Field;
import scw.mapper.Fields;
import scw.orm.sql.AbstractSqlDialect;
import scw.orm.sql.IndexInfo;
import scw.orm.sql.PaginationSql;
import scw.orm.sql.SqlDialectException;
import scw.orm.sql.SqlType;
import scw.orm.sql.annotation.Counter;
import scw.orm.sql.annotation.IndexMethod;
import scw.orm.sql.annotation.IndexOrder;
import scw.orm.sql.annotation.Table;
import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.sql.orm.Column;

public class MysqlDialect extends AbstractSqlDialect {
	private static final String DUPLICATE_KEY = " ON DUPLICATE KEY UPDATE ";
	private static final String IF = "IF(";
	private static final String LAST_INSERT_ID_SQL = "select last_insert_id()";

	private static Logger logger = LoggerFactory.getLogger(MysqlDialect.class);

	@Override
	public Sql getById(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException {
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableName);

		Fields primaryKeys = getPrimaryKey(entityClass);
		Iterator<Field> iterator = primaryKeys.iterator();
		Iterator<Object> valueIterator = Arrays.asList(ids).iterator();
		if (iterator.hasNext() && valueIterator.hasNext()) {
			sb.append(WHERE);
		}

		Object[] params = new Object[ids.length];
		int i = 0;
		while (iterator.hasNext() && valueIterator.hasNext()) {
			Field column = iterator.next();
			Object value = valueIterator.next();
			params[i++] = toDataBaseValue(value, TypeDescriptor.forObject(value));
			appendFieldName(sb, column.getGetter());
			sb.append("=?");
			if (iterator.hasNext() && valueIterator.hasNext()) {
				sb.append(AND);
			}
		}
		return new SimpleSql(sb.toString(), params);
	}

	@Override
	public <T> Sql save(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException {
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Field> iterator = getFields(entityClass).iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			if (isAutoIncrement(column.getSetter())) {
				continue;
			}

			if (cols.length() > 0) {
				cols.append(",");
				values.append(",");
			}

			appendFieldName(cols, column.getGetter());
			values.append("?");
			params.add(getDataBaseValue(entity, column));
		}
		sql.append(INSERT_INTO_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append("(");
		sql.append(cols);
		sql.append(VALUES);
		sql.append(values);
		sql.append(")");
		return new SimpleSql(sql.toString(), params.toArray());
	}

	@Override
	public <T> Sql delete(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException {
		Fields primaryKeys = getPrimaryKey(entityClass).shared();
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		List<Object> params = new ArrayList<Object>(primaryKeys.size());
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append(WHERE);
		Iterator<Field> iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			appendFieldName(sql, column.getGetter());
			sql.append("=?");
			params.add(getDataBaseValue(entity, column));
			if (iterator.hasNext()) {
				sql.append(AND);
			}
		}
		return new SimpleSql(sql.toString(), params.toArray());
	}

	@Override
	public Sql deleteById(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException {
		Fields primaryKeys = getPrimaryKey(entityClass);
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (primaryKeys.size() != ids.length) {
			throw new ParameterException("主键数量不一致:" + tableName);
		}

		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append(WHERE);

		int i = 0;
		Object[] params = new Object[ids.length];
		Iterator<Field> iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			appendFieldName(sql, column.getGetter());
			sql.append("=?");
			params[i] = toDataBaseValue(ids[i]);
			i++;
			if (iterator.hasNext()) {
				sql.append(AND);
			}
		}
		return new SimpleSql(sql.toString(), params);
	}

	@Override
	public <T> Sql update(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException {
		Fields primaryKeys = getPrimaryKey(entityClass).shared();
		if (primaryKeys.size() == 0) {
			throw new NullPointerException(tableName + " not found primary key");
		}

		Fields notPrimaryKeys = getNotPrimaryKeys(entityClass).shared();
		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append(SET);
		List<Object> params = new ArrayList<Object>(notPrimaryKeys.size());
		Iterator<Field> iterator = notPrimaryKeys.iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			appendFieldName(sb, column.getGetter());
		}

		sb.append(WHERE);
		iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			Field column = iterator.next();
			appendFieldName(sb, column.getGetter());
			sb.append("=?");
			params.add(getDataBaseValue(entity, column));
			if (iterator.hasNext()) {
				sb.append(AND);
			}
		}
		return new SimpleSql(sb.toString(), params.toArray());
	}

	@Override
	public <T> Sql saveOrUpdate(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException {
		Fields primaryKeys = getPrimaryKey(entityClass);
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

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
			Object v = getDataBaseValue(entity, column);
			Counter counter = getCounter(column.getGetter());
			if (counter == null) {
				appendFieldName(sb, column.getGetter());
				sb.append("=?");
				params.add(v);
			} else {
				if (v == null) {
					logger.warn("{}中计数器字段{}的值为空", entityClass, column);
					appendFieldName(sb, column.getGetter());
					sb.append("=?");
					params.add(v);
				} else {
					appendFieldName(sb, column.getGetter());
					sb.append("=");
					sb.append(IF);
					appendFieldName(sb, column.getGetter());
					sb.append("+").append(v);
					sb.append(">=").append(counter.min());
					sb.append(AND);
					appendFieldName(sb, column.getGetter());
					sb.append("+").append(v);
					sb.append("<=").append(counter.max());
					sb.append(",");
					appendFieldName(sb, column.getGetter());
					sb.append("+?");
					params.add(v);
					sb.append(",");
					appendFieldName(sb, column.getGetter());
					sb.append(")");
				}
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		return new SimpleSql(sb.toString(), params.toArray());
	}

	@Override
	public Sql createTable(String tableName, Class<?> entityClass) throws SqlDialectException {
		StringBuilder sb = new StringBuilder();
		sb.append(getCreateTablePrefix());
		sb.append(" ");
		keywordProcessing(sb, tableName);
		sb.append(" (");

		Fields primaryKeys = getPrimaryKey(entityClass);
		Fields columns = getFields(entityClass).duplicateRemoval();
		Iterator<Field> iterator = columns.iterator();
		for(Field col : columns) {
			SqlType sqlType = getSqlTypeMapping().getSqlType(col.getGetter().getType());
			appendFieldName(sb, col.getGetter());

			sb.append(" ");
			sb.append(sqlType.getName());
			
			if (primaryKeys.size() == 1) {
				if(getObjectRelationalMapping().isPrimaryKey(col.getGetter())) {
					sb.append(" PRIMARY KEY");
				}
				
				if(isAutoIncrement(col.getGetter())) {
					sb.append(" AUTO_INCREMENT");
				}
			}

			if (col.isUnique()) {
				sb.append(" UNIQUE");
			}

			if (!col.isNullable()) {
				sb.append(" not null");
			}

			if (StringUtils.isNotEmpty(col.getDescription())) {
				sb.append(" comment \'").append(col.getDescription()).append("\'");
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
	public PaginationSql toPaginationSql(Sql sql, long start, int limit) throws SqlDialectException {
		String str = sql.getSql();
		int fromIndex = str.indexOf(" from ");// ignore select
		if (fromIndex == -1) {
			fromIndex = str.indexOf(" FROM ");
		}

		if (fromIndex == -1) {
			throw new IndexOutOfBoundsException(str);
		}

		String whereSql;
		int orderIndex = str.lastIndexOf(" order by ");
		if (orderIndex == -1) {
			orderIndex = str.lastIndexOf(" ORDER BY ");
		}

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

}
