package io.basc.framework.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EditableSql implements Sql, Serializable {
	private static final long serialVersionUID = 1L;
	private StringBuilder sql = new StringBuilder();
	private List<Object> params = new ArrayList<Object>(8);
	private boolean storedProcedure = false;

	@Override
	public String getSql() {
		return sql.toString();
	}

	@Override
	public Object[] getParams() {
		return params.toArray();
	}

	@Override
	public boolean isStoredProcedure() {
		return storedProcedure;
	}

	public void setSql(StringBuilder sql) {
		this.sql = sql;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}

	public void setStoredProcedure(boolean storedProcedure) {
		this.storedProcedure = storedProcedure;
	}

	public EditableSql append(CharSequence sql) {
		this.sql.append(sql);
		return this;
	}

	public EditableSql addParams(Object... params) {
		for (Object param : params) {
			this.params.add(param);
		}
		return this;
	}

	public boolean hasSql() {
		return sql.length() > 0;
	}

	public boolean hasParams() {
		return !params.isEmpty();
	}

	public EditableSql append(CharSequence sql, Object... params) {
		return append(sql).addParams(params);
	}

	public EditableSql append(Sql sql) {
		if (sql.isStoredProcedure()) {
			this.storedProcedure = true;
		}
		return append(sql.getSql(), sql.getParams());
	}
	
	@Override
	public String toString() {
		return SqlUtils.toString(sql.toString(), params.toArray());
	}
}
