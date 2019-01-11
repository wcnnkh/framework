package scw.db.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scw.database.SQL;

public class WhereSql implements SQL {
	private static final long serialVersionUID = 1L;
	private List<Object> paramList;
	private StringBuilder sb;

	public void where(String whereSql, Object... params) {
		checkAnd();

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

	private void checkParams() {
		if (paramList == null) {
			paramList = new ArrayList<Object>();
		}
	}

	public void in(String name, Collection<?> collection) {
		if (collection == null || collection.isEmpty()) {
			return;
		}

		checkAnd();
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
		return paramList == null ? new Object[0] : paramList.toArray();
	}

	public SQL assembleSql(String beforeSql, String afterSql) {
		if (sb == null || sb.length() == 0) {
			return new SimpleSQL(beforeSql + " " + afterSql, getParams());
		} else {
			StringBuilder sql = new StringBuilder();
			sql.append(beforeSql);
			sql.append(" where ");
			sql.append(sb);
			sql.append(" ");
			sql.append(afterSql);
			return new SimpleSQL(sql.toString(), getParams());
		}
	}
}
