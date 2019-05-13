package scw.mq.rabbit;

import java.lang.reflect.Method;
import java.util.Map;

import com.rabbitmq.client.AMQP.BasicProperties;

public final class RabbitConsumerAnnotationDefinition<T> implements RabbitConsumerDefinition<Object[]> {
	private final RabbitConsumer rabbitConsumer;
	private final Object obj;
	private final Method method;

	public RabbitConsumerAnnotationDefinition(Object obj, Method method) {
		rabbitConsumer = method.getAnnotation(RabbitConsumer.class);
		this.method = method;
		this.obj = obj;
	}

	public boolean isEmpty() {
		return rabbitConsumer == null;
	}

	public String getQueueName() {
		return rabbitConsumer.queueName();
	}

	public boolean isDurable() {
		return rabbitConsumer.durable();
	}

	public boolean isExclusive() {
		return rabbitConsumer.exclusive();
	}

	public boolean isAutoDelete() {
		return rabbitConsumer.autoDelete();
	}

	public Map<String, Object> getArguments() {
		return null;
	}

	public void consumer(BasicProperties properties, Object[] message) throws Throwable{
		method.invoke(obj, message);
	}
}
