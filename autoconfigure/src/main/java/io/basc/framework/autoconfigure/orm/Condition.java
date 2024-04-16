package io.basc.framework.autoconfigure.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Condition {
	/**
	 * endWith
	 * 
	 * <p>
	 * equ =
	 * 
	 * <p>
	 * geq &gt;=
	 * 
	 * <p>
	 * gtr &gt;
	 * 
	 * <p>
	 * in
	 * 
	 * <p>
	 * leq &lt;=
	 * 
	 * <p>
	 * like
	 * 
	 * <p>
	 * lss &lt;
	 * 
	 * <p>
	 * neq !
	 * 
	 * <p>
	 * search %a%b%c
	 * 
	 * <p>
	 * startWith
	 * 
	 * <p>
	 * 
	 * @return 条件
	 */
	String value();
}
