package io.basc.framework.sql;

/**
 * sql语句
 * 
 * @author shuchaowen
 *
 */
public interface Sql {
	/**
	 * sql语句
	 * 
	 * @return
	 */
	String getSql();

	/**
	 * sql中的参数
	 * 
	 * @return
	 */
	Object[] getParams();

	/**
	 * 是否是存储过程
	 * 
	 * @return
	 */
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