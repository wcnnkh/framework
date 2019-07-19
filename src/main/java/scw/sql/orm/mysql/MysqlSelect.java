package scw.sql.orm.mysql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scw.core.exception.ParameterException;
import scw.core.utils.CollectionUtils;
import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMOperations;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.Select;
import scw.sql.orm.TableInfo;
import scw.sql.orm.result.ResultSet;

public final class MysqlSelect extends Select {
	private static final long serialVersionUID = 1L;
	/**
	 * 表和表的别名
	 */
	private StringBuilder whereSql;
	private List<Object> paramList;
	private StringBuilder orderBySql;

	public MysqlSelect(ORMOperations db) {
		super(db);
	}

	private void checkWhereInit() {
		if (whereSql == null) {
			whereSql = new StringBuilder();
		}

		if (paramList == null) {
			paramList = new ArrayList<Object>();
		}
	}

	private void checkOrderInit() {
		if (orderBySql == null) {
			orderBySql = new StringBuilder();
		}
	}

	@Override
	public Select whereAndValue(Class<?> tableClass, String name, Object value) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		if (!tableInfo.isTable()) {
			throw new ParameterException(tableClass.getName() + "not found @Table");
		}

		String tableName = getTableName(tableClass);
		checkWhereInit();
		if (whereSql.length() != 0) {
			whereSql.append(UpdateSQL.AND);
		}

		keywordProcessing(whereSql, tableName, tableInfo.getColumnInfo(name).getName());
		whereSql.append("=?");
		paramList.add(value);

		addSelectTable(tableName);
		return this;
	}

	@Override
	public Select whereOrValue(Class<?> tableClass, String name, Object value) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		if (!tableInfo.isTable()) {
			throw new ParameterException(tableClass.getName() + "not found @Table");
		}

		String tableName = getTableName(tableClass);
		checkWhereInit();
		if (whereSql.length() != 0) {
			whereSql.append(UpdateSQL.OR);
		}

		keywordProcessing(whereSql, tableName, tableInfo.getColumnInfo(name).getName());
		whereSql.append("=?");
		paramList.add(value);
		addSelectTable(tableName);
		return this;
	}

	@Override
	public Select whereAndIn(Class<?> tableClass, String name, Collection<?> values) {
		if (values == null || values.isEmpty() || name == null || tableClass == null) {
			throw new NullPointerException();
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		if (!tableInfo.isTable()) {
			throw new ParameterException(tableClass.getName() + "not found @Table");
		}

		String tableName = getTableName(tableClass);
		checkWhereInit();
		if (whereSql.length() != 0) {
			whereSql.append(" and ");
		}

		keywordProcessing(whereSql, tableName, tableInfo.getColumnInfo(name).getName());
		whereSql.append(" in(");
		Iterator<?> iterator = values.iterator();
		while (iterator.hasNext()) {
			paramList.add(iterator.next());
			whereSql.append("?");
			if (iterator.hasNext()) {
				whereSql.append(",");
			}
		}
		whereSql.append(")");
		addSelectTable(tableName);
		return this;
	}

	@Override
	public Select whereOrIn(Class<?> tableClass, String name, Collection<?> values) {
		if (values == null || values.isEmpty() || name == null || tableClass == null) {
			throw new NullPointerException();
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		if (!tableInfo.isTable()) {
			throw new ParameterException(tableClass.getName() + "not found @Table");
		}

		String tableName = getTableName(tableClass);
		checkWhereInit();
		if (whereSql.length() != 0) {
			whereSql.append(" or ");
		}

		keywordProcessing(whereSql, tableName, tableInfo.getColumnInfo(name).getName());
		whereSql.append(" in(");
		Iterator<?> iterator = values.iterator();
		while (iterator.hasNext()) {
			paramList.add(iterator.next());
			whereSql.append("?");
			if (iterator.hasNext()) {
				whereSql.append(",");
			}
		}
		whereSql.append(")");
		addSelectTable(tableName);
		return this;
	}

	@Override
	public Select desc(Class<?> tableClass, Collection<String> nameList) {
		if (nameList == null || tableClass == null || nameList.isEmpty()) {
			throw new NullPointerException();
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		if (!tableInfo.isTable()) {
			throw new ParameterException(tableClass.getName() + "not found @Table");
		}

		String tableName = getTableName(tableClass);

		checkOrderInit();
		Iterator<String> iterator = nameList.iterator();
		while (iterator.hasNext()) {
			ColumnInfo columnInfo = tableInfo.getColumnInfo(iterator.next());
			keywordProcessing(orderBySql, tableName, columnInfo.getName());
			if (iterator.hasNext()) {
				orderBySql.append(",");
			}
		}

		orderBySql.append(" desc");
		addSelectTable(tableName);
		return this;
	}

	@Override
	public Select asc(Class<?> tableClass, Collection<String> nameList) {
		if (nameList == null || tableClass == null || nameList.isEmpty()) {
			throw new NullPointerException();
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		if (!tableInfo.isTable()) {
			throw new ParameterException(tableClass.getName() + "not found @Table");
		}

		String tableName = getTableName(tableClass);

		checkOrderInit();
		Iterator<String> iterator = nameList.iterator();
		while (iterator.hasNext()) {
			ColumnInfo columnInfo = tableInfo.getColumnInfo(iterator.next());
			keywordProcessing(orderBySql, tableName, columnInfo.getName());
			if (iterator.hasNext()) {
				orderBySql.append(",");
			}
		}

		orderBySql.append(" asc");
		addSelectTable(tableName);
		return this;
	}

	@Override
	public long count() {
		Sql sql = toSQL("count(*)", false);
		scw.sql.orm.result.ResultSet resultSet = orm.select(sql);
		Long count = resultSet.getFirst().get(Long.class);
		return count == null ? 0 : count;
	}

	@Override
	public ResultSet getResultSet() {
		return orm.select(toSQL("*", true));
	}

	@Override
	public ResultSet getResultSet(long begin, int limit) {
		Sql sql = toSQL("*", true);
		Object[] args;
		if (sql.getParams() == null) {
			args = new Object[2];
		} else {
			args = new Object[sql.getParams().length + 2];
		}

		if (sql.getParams() != null) {
			System.arraycopy(sql.getParams(), 0, args, 0, sql.getParams().length);
		}

		args[args.length - 2] = begin;
		args[args.length - 1] = limit;

		return orm.select(new SimpleSql(sql.getSql() + " limit ?,?", args));
	}

	@Override
	public Sql toSQL(String select, boolean order) {
		return new SimpleSql(getSql(select, order), getParams());
	}

	@Override
	public Select whereAnd(String where, Collection<?> values) {
		checkWhereInit();
		if (whereSql.length() != 0) {
			whereSql.append(" and ");
		}
		whereSql.append(where);

		if (values != null) {
			paramList.addAll(values);
		}
		return this;
	}

	@Override
	public Select whereOr(String where, Collection<?> values) {
		checkWhereInit();
		if (whereSql.length() != 0) {
			whereSql.append(" or ");
		}
		whereSql.append(where);

		if (values != null) {
			paramList.addAll(values);
		}
		return this;
	}

	public String getSql(String select, boolean order) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ").append(select).append(" from ");
		sb.append(getSelectTables());

		String where = getAssociationWhere();
		if (whereSql != null && whereSql.length() != 0) {
			sb.append(" where ");
			sb.append(whereSql);
			if (where != null && where.length() != 0) {
				sb.append(" and ");
				sb.append(where);
			}

		} else {
			if (where != null && where.length() != 0) {
				sb.append(" where ");
				sb.append(where);
			}
		}

		if (order) {
			if (orderBySql != null && orderBySql.length() != 0) {
				sb.append(" order by ");
				sb.append(orderBySql);
			}
		}
		return sb.toString();
	}

	public String getSql() {
		return getSql("*", true);
	}

	public Object[] getParams() {
		return CollectionUtils.isEmpty(paramList) ? new Object[0] : paramList.toArray(new Object[paramList.size()]);
	}
}
