package io.basc.framework.orm.sql.annotation;

/**
 * 索引的排序方式
 * 
 * @author shuchaowen
 *
 */
public enum IndexOrder {
	/**
	 * 默认的，一般是没有
	 */
	DEFAULT,
	
	/**
	 * 降序
	 */
	DESC, 
	/**
	 * 升序
	 */
	ASC;
}
