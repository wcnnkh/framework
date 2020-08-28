package scw.sql;

import java.io.Serializable;

/**
 * sql语句
 * @author shuchaowen
 *
 */
public interface Sql extends Serializable {
	/**
	 * sql语句
	 * @return
	 */
	String getSql();

	/**
	 * sql中的参数
	 * @return
	 */
	Object[] getParams();

	/**
	 * 是否是存储过程
	 * 
	 * @return
	 */
	boolean isStoredProcedure();
}