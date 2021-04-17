package scw.rmi.beans;

import scw.core.annotation.AliasFor;

public @interface RmiClient {
	@AliasFor("host")
	String value() default "";
	
	String host() default "";
}
