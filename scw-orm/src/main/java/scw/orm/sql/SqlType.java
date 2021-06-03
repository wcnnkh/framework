package scw.orm.sql;

import java.sql.Date;

import scw.lang.Nullable;

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
	 * 返回此类型支持的最大长度
	 * @return 返回空就说明无限制
	 */
	@Nullable
	Long getMaxLength();
}
