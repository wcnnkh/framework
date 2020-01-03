package scw.mq.amqp;

public interface AmqpQueueConfig {
	String getRoutingKey();

	String getQueueName();

	boolean isDurable();

	boolean isExclusive();

	boolean isAutoDelete();
}
