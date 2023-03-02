package io.basc.framework.sql;

/**
 * sql语句
 * 
 * @author wcnnkh
 *
 */
public interface Sql {
	String getSql();

	Object[] getParams();

	default boolean isStoredProcedure() {
		return false;
	}

	default Sql trim() {
		return new SimpleSql(isStoredProcedure(), getSql().trim(), getParams());
	}

	public static Sql of(String sql, Object... params) {
		return new SimpleSql(sql, params);
	}

	public static Sql ofStoredProcedure(String sql, Object... params) {
		return new SimpleSql(true, sql, params);
	}
}