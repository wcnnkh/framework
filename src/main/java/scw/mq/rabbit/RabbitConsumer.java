package scw.mq.rabbit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RabbitConsumer {
	public String queueName();
	
	/**
	 * 是否持久化
	 * @return
	 */
	public boolean durable() default false;
	
	/**
	 * 是否存在排他性
	 * @return
	 */
	public boolean exclusive() default false;
	
	public boolean autoDelete() default true;
}
