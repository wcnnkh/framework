package scw.sql;

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
}