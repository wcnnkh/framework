package io.basc.framework.rabbitmq;

import io.basc.framework.amqp.Message;
import io.basc.framework.amqp.MessageListener;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitmqMessageListener extends DefaultConsumer {
	private static Logger logger = LogManager.getLogger(RabbitmqMessageListener.class);
	private final MessageListener<byte[]> messageListener;
	private final boolean multiple;

	public RabbitmqMessageListener(Channel channel, MessageListener<byte[]> messageListener, boolean multiple) {
		super(channel);
		this.messageListener = messageListener;
		this.multiple = multiple;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
		Message<byte[]> message = RabbitmqUitls.toMessage(properties, body);
		try {
			messageListener.onMessage(envelope.getExchange(), envelope.getRoutingKey(), message);
			getChannel().basicAck(envelope.getDeliveryTag(), multiple);
		} catch (IOException e) {
			logger.error(e, "consumerTag={}, envelope={}, properties={}, body={}", consumerTag, envelope, properties,
					body);
			getChannel().basicReject(envelope.getDeliveryTag(), true);// 将消息分配给其他消费者
		}
	}
}
