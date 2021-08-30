package io.basc.framework.rmi.beans;

import io.basc.framework.core.annotation.AliasFor;

public @interface RmiClient {
	@AliasFor("host")
	String value() default "";
	
	String host() default "";
}
