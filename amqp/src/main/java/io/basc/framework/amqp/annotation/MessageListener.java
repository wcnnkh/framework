package io.basc.framework.amqp.annotation;

import io.basc.framework.amqp.Exchange;
import io.basc.framework.context.annotation.Indexed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface MessageListener {
	String routingKey();

	String queueName();

	boolean durable() default true;

	boolean exclusive() default false;

	boolean autoDelete() default false;

	Class<? extends Exchange> exchange() default Exchange.class;
}
