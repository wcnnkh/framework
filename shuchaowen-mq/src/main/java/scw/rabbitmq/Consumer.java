package scw.rabbitmq;

import com.rabbitmq.client.Envelope;

public interface Consumer {
	void handleDelivery(Exchange exchange, String consumerTag, Envelope envelope, Message message) throws Throwable;
}
