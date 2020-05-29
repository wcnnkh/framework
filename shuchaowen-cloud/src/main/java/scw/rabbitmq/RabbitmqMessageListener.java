package scw.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import scw.amqp.Message;
import scw.amqp.MessageListener;

public class RabbitmqMessageListener extends DefaultConsumer {
	private final MessageListener messageListener;
	private final boolean multiple;

	public RabbitmqMessageListener(Channel channel, MessageListener messageListener, boolean multiple) {
		super(channel);
		this.messageListener = messageListener;
		this.multiple = multiple;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
		Message message = RabbitmqUitls.toMessage(properties, body);
		messageListener.onMessage(envelope.getExchange(), envelope.getRoutingKey(), message);
		getChannel().basicAck(envelope.getDeliveryTag(), multiple);
	}
}
