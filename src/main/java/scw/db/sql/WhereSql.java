package scw.db.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scw.common.utils.CollectionUtils;
import scw.common.utils.StringUtils;
import scw.database.SQL;

public class WhereSql implements SQL {
	private static final long serialVersionUID = 1L;
	private List<Object> paramList;
	private StringBuilder sb;

	/**
	 * 默认是and
	 * 
	 * @param whereSql
	 * @param params
	 */
	public void where(String whereSql, Object... params) {
		and(whereSql, params);
	}

	public void and(String whereSql, Object... params) {
		checkAnd();
		privateWhere(whereSql, params);
	}

	public void or(String whereSql, Object... params) {
		checkOr();
		privateWhere(whereSql, params);
	}

	private void privateWhere(String whereSql, Object... params) {
		sb.append(whereSql);

		if (params != null && params.length != 0) {
			checkParams();

			for (Object o : params) {
				paramList.add(o);
			}
		}
	}

	private void checkAnd() {
		if (sb == null) {
			sb = new StringBuilder();
		}

		if (sb.length() != 0) {
			sb.append(" and ");
		}
	}

	private void checkOr() {
		if (sb == null) {
			sb = new StringBuilder();
		}

		if (sb.length() != 0) {
			sb.append(" or ");
		}
	}

	private void checkParams() {
		if (paramList == null) {
			paramList = new LinkedList<Object>();
		}
	}

	/**
	 * 默认是and
	 * 
	 * @param name
	 * @param collection
	 */
	public void in(String name, Collection<?> collection) {
		andIn(name, collection);
	}

	public void andIn(String name, Collection<?> collection) {
		if (collection == null || collection.isEmpty()) {
			return;
		}

		checkAnd();
		privateIn(name, collection);
	}

	public void orIn(String name, Collection<?> collection) {
		if (collection == null || collection.isEmpty()) {
			return;
		}

		checkOr();
		privateIn(name, collection);
	}

	private void privateIn(String name, Collection<?> collection) {
		checkParams();
		sb.append(name);
		sb.append(" in (");
		Iterator<?> iterator = collection.iterator();
		while (iterator.hasNext()) {
			Object param = iterator.next();
			sb.append("?");
			if (iterator.hasNext()) {
				sb.append(",");
			}
			paramList.add(param);
		}
		sb.append(")");
	}

	public String getSql() {
		return sb == null ? null : sb.toString();
	}

	public Object[] getParams() {
		return paramList == null ? CollectionUtils.EMPTY_ARRAY : paramList.toArray();
	}

	public SQL assembleSql(String beforeSql, String afterSql, Object... params) {
		Object[] arr;
		if (paramList == null || paramList.isEmpty()) {
			arr = params;
		} else {
			List<Object> list = new ArrayList<Object>(paramList);
			for (Object o : params) {
				list.add(o);
			}
			arr = list.toArray(new Object[list.size()]);
		}

		if (sb == null || sb.length() == 0) {
			if (StringUtils.isNull(afterSql)) {
				return new SimpleSQL(beforeSql, arr);
			} else {
				return new SimpleSQL(beforeSql + " " + afterSql, arr);
			}
		} else {
			StringBuilder sql = new StringBuilder();
			sql.append(beforeSql);
			sql.append(" where ");
			sql.append(sb);
			if (!StringUtils.isNull(afterSql)) {
				sql.append(" ");
				sql.append(afterSql);
			}
			return new SimpleSQL(sql.toString(), arr);
		}
	}

	public boolean isStoredProcedure() {
		return false;
	}
}
