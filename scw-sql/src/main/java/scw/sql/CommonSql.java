package scw.sql;

import java.util.ArrayList;
import java.util.List;

public class CommonSql extends SerializableSql{
	private static final long serialVersionUID = 1L;
	private StringBuilder sb;
	private List<Object> paramList;

	public CommonSql() {
	};

	public CommonSql(String sql, Object... params) {
		sb = new StringBuilder(sql);
		if (params != null) {
			for (Object obj : params) {
				addParam(obj);
			}
		}
	}

	public CommonSql before(Object str) {
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

	public CommonSql append(Object str) {
		return after(str);
	}

	public CommonSql after(Object str) {
		if (sb == null) {
			sb = new StringBuilder();
		}

		if (str != null) {
			sb.append(str);
		}
		return this;
	}

	public CommonSql addParam(Object param) {
		if (paramList == null) {
			paramList = new ArrayList<Object>();
		}
		paramList.add(param);
		return this;
	}

	public CommonSql setParam(int index, Object param) {
		if (paramList == null) {
			paramList = new ArrayList<Object>();
		}

		paramList.set(index, param);
		return this;
	}

	public Object[] getParams() {
		return paramList.toArray();
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
}
