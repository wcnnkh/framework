package shuchaowen.core.db.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import shuchaowen.core.db.cache.CacheFactory;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * 默认的表名
	 * @return
	 */
	public String name() default "";
	
	public String engine() default "InnoDB";
	
	public String charset() default "utf8";
	
	public String row_format() default "COMPACT";
	
	/**
	 * 是否自动创建
	 * @return
	 */
	public boolean create() default true;
	
	/**
	 * 是否遍历父级字段
	 * 默认是true
	 * @return
	 */
	public boolean parent() default true;
	
	/**
	 * 默认情况下不使用缓存
	 * @return
	 */
	public Class<? extends CacheFactory> cacheFactory() default CacheFactory.class;
}
