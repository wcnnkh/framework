package scw.rabbitmq;

import scw.amqp.MessageProperties;

public interface RabbitmqRetrySender {
	void beginSend(MessageProperties messageProperties, byte[] body);
}
