package io.basc.framework.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Condition {
	/**
	 * endWith<br/>
	 * equ =<br/>
	 * geq >=<br/>
	 * gtr ><br/>
	 * in <br/>
	 * leq <=<br/>
	 * like <br/>
	 * lss <<br/>
	 * neq !<br/>
	 * search %a%b%c<br/>
	 * startWith<br/>
	 * 
	 * @return
	 */
	String value();
}
