package io.basc.framework.amqp.annotation;

import io.basc.framework.amqp.Exchange;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageListener {
	public String routingKey();

	public String queueName();

	public boolean durable() default true;

	public boolean exclusive() default false;

	public boolean autoDelete() default false;

	public Class<? extends Exchange> exchange() default Exchange.class;
}
