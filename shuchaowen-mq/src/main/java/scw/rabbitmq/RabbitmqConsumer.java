package scw.rabbitmq;

import com.rabbitmq.client.Envelope;

public interface RabbitmqConsumer {
	void handleDelivery(RabbitmqExchange exchange, String consumerTag, Envelope envelope, RabbitmqMessage message) throws Throwable;
}
