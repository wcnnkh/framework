package scw.sql.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.sql.orm.enums.CasType;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cas {
	/**
	 * 指定cas的使用方式
	 * @return
	 */
	public CasType value() default CasType.DEFAULT;
}
