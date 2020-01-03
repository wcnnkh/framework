package scw.mq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.mq.ConsumerFactory;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumer {
	public String name();

	public Class<? extends ConsumerFactory> factory() default ConsumerFactory.class;
}
