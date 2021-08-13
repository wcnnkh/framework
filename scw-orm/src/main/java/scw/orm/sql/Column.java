package scw.orm.sql;

import scw.lang.Nullable;
import scw.orm.PropertyDescriptor;

/**
 * 数据库的一个列
 * @author shuchaowen
 *
 */
public interface Column extends PropertyDescriptor{
	boolean isAutoIncrement();

	@Nullable
	String getComment();
	
	Class<?> getType();
	
	@Nullable
	Long getMaxLength();
	
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
	
	String getCharsetName();
}
