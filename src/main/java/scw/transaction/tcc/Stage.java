package scw.transaction.tcc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TCC步骤的定义
 * @author shuchaowen
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Stage {
	public String name();

	/**
	 * 参数的插入是否以名称的方式调用，默认为索引的方式
	 * @return
	 */
	public boolean parameterNameMapping() default false;
	
	/**
	 * 将try调用后的结果插入到指定位置
	 * @return
	 */
	public int tryResultSetParameterIndex() default -1;
}
