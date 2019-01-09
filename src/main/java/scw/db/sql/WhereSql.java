package scw.db.sql;

import java.util.ArrayList;
import java.util.List;

import scw.database.SQL;

public class WhereSql implements SQL {
	private static final long serialVersionUID = 1L;
	private List<Object> paramList;
	private StringBuilder sb;

	public void where(String whereSql, Object... params) {
		if (sb == null) {
			sb = new StringBuilder();
		}

		if (sb.length() != 0) {
			sb.append(" and ");
		}
		sb.append(whereSql);

		if (params != null && params.length != 0) {
			if (paramList == null) {
				paramList = new ArrayList<Object>();
			}

			for (Object o : params) {
				paramList.add(o);
			}
		}
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
