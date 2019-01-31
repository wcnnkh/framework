package scw.db.sql;

import java.util.LinkedList;
import java.util.List;

import scw.common.utils.CollectionUtils;
import scw.jdbc.Sql;

public class CommonSQL implements Sql {
	private static final long serialVersionUID = 1L;
	private StringBuilder sb;
	private List<Object> paramList;

	public CommonSQL() {
	};

	public CommonSQL(String sql, Object... params) {
		sb = new StringBuilder(sql);
		if (params != null) {
			for (Object obj : params) {
				addParam(obj);
			}
		}
	}

	public CommonSQL before(Object str) {
		if (sb == null) {
			sb = new StringBuilder();
		}

		if (str != null) {
			sb.insert(0, str);
		}
		return this;
	}

	public int length() {
		return sb == null ? 0 : sb.length();
	}

	public CommonSQL append(Object str) {
		return after(str);
	}

	public CommonSQL after(Object str) {
		if (sb == null) {
			sb = new StringBuilder();
		}

		if (str != null) {
			sb.append(str);
		}
		return this;
	}

	public CommonSQL addParam(Object param) {
		if (paramList == null) {
			paramList = new LinkedList<Object>();
		}
		paramList.add(param);
		return this;
	}

	public CommonSQL setParam(int index, Object param) {
		if (paramList == null) {
			paramList = new LinkedList<Object>();
		}

		paramList.set(index, param);
		return this;
	}

	public Object[] getParams() {
		return paramList == null ? CollectionUtils.EMPTY_ARRAY : paramList.toArray();
	}

	public void clear() {
		if (sb != null) {
			sb.delete(0, sb.length());
		}

		if (paramList != null) {
			paramList.clear();
		}
	}

	public String getSql() {
		return sb == null ? null : sb.toString();
	}

	public boolean isStoredProcedure() {
		return false;
	}
}
