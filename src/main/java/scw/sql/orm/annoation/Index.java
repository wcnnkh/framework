package scw.sql.orm.annoation;

import scw.sql.orm.enums.IndexMethod;
import scw.sql.orm.enums.IndexOrder;
import scw.sql.orm.enums.IndexType;

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
