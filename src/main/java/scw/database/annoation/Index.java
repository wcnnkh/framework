package scw.database.annoation;

import scw.database.enums.IndexMethod;
import scw.database.enums.IndexOrder;
import scw.database.enums.IndexType;

/**
 * 索引
 * 
 * @author shuchaowen
 *
 */
public @interface Index {
	public String name() default "";

	public IndexType type() default IndexType.DEFAULT;

	/**
	 * 默认是全部长度
	 * 
	 * @return
	 */
	public int length() default -1;

	/**
	 * 默认由数据库自己选择
	 * 
	 * @return
	 */
	public IndexMethod method() default IndexMethod.DEFAULT;

	/**
	 * 索引的排序方式
	 * @return
	 */
	public IndexOrder order() default IndexOrder.DEFAULT;
}
