package scw.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 流水号
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SequenceIdGenerator {
	/**
	 * 是否是流水号，否则就是流水号创建时间
	 * 
	 * @return
	 */
	public boolean value() default true;
}
