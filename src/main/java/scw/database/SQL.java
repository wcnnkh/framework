package scw.database;

import java.io.Serializable;

public interface SQL extends Serializable {
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