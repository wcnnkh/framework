package io.basc.framework.amqp.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.amqp.boot.MethodInvokerExchange;
import io.basc.framework.beans.factory.annotation.Indexed;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface MessageListener {
	String routingKey();

	String queueName();

	boolean durable() default true;

	boolean exclusive() default false;

	boolean autoDelete() default false;

	Class<? extends MethodInvokerExchange> exchange() default MethodInvokerExchange.class;
}
