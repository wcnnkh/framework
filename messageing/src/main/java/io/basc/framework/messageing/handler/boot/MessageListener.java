package io.basc.framework.messageing.handler.boot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.basc.framework.messageing.SubscribableChannel;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageListener {
	/**
	 * {@link SubscribableChannel}
	 * 
	 * @return
	 */
	String channel();

	String handleMessageConverter() default "";
}
