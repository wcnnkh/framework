package scw.orm.sql;

import java.sql.Date;

/**
 * sql类型
 * 
 * @author shuchaowen
 *
 */
public interface SqlType {
	/**
	 * 类型名称<br/>
	 * varchar<br/>
	 * bigint<br/>
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 类型<br/>
	 * 
	 * @see Date
	 * @see Integer
	 * @return
	 */
	Class<?> getType();
	
	/**
	 * 如里小于等于0可以忽略
	 * @return
	 */
	int getLength();
}
