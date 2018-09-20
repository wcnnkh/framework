package shuchaowen.core.beans.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import shuchaowen.core.beans.proxy.ProxyFactory;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Proxy {
	public Class<? extends ProxyFactory> value() default ProxyFactory.class;
}
