package scw.orm.sql;

import scw.lang.Nullable;
import scw.orm.PropertyDescribe;

/**
 * 数据库的一个列
 * @author shuchaowen
 *
 */
public interface Column extends PropertyDescribe{
	boolean isAutoIncrement();

	@Nullable
	String getComment();

	SqlType getSqlType();
	
	/**
	 * 是否是唯一索引
	 * @return
	 */
	boolean isUnique();
	
	/**
	 * 索引名称
	 * @return
	 */
	@Nullable
	String getIndexName();
	
	boolean isNullable();
}
