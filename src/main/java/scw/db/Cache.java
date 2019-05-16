package scw.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import scw.db.cache.CacheType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Cache {
	/**
	 * 默认过期时间是两天
	 * 
	 * @return
	 */
	public int exp() default 2;

	public TimeUnit timeUnit() default TimeUnit.DAYS;
	
	/**
	 * 缓存方式
	 * @return
	 */
	public CacheType type() default CacheType.lazy;
	
	/**
	 * 是否对单个主键的数据索引进行保存
	 * @return
	 */
	public boolean fullKeys() default false;
}
