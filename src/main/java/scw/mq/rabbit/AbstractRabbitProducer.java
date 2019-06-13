package scw.mq.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import scw.beans.annotation.AsyncComplete;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.serializer.support.JavaSerializer;
import scw.mq.Producer;

public abstract class AbstractRabbitProducer<T> extends ChannelFactory implements Producer<T> {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public AbstractRabbitProducer(ConnectionFactory connectionFactory, String exchange, String exchangeType)
			throws IOException, TimeoutException {
		super(connectionFactory, exchange, exchangeType);
	}

	protected abstract boolean isMandatory(String name);

	protected abstract boolean isImmediate(String name);

	protected abstract BasicProperties getBasicProperties(String name);

	@AsyncComplete
	public void push(String name, T message) {
		if (message == null) {
			logger.error("生产内容为空，routingKey:{}", name);
			return;
		}

		Channel channel = getChannel(name);
		if (channel == null) {
			logger.error("无法获取channel, routingKey:{}", name);
			return;
		}

		try {
			channel.basicPublish(getExchange(), name, isMandatory(name), isImmediate(name), getBasicProperties(name),
					JavaSerializer.SERIALIZER.serialize(message));
		} catch (IOException e) {
			throw new RuntimeException("生产者routingKey:" + name, e);
		}
	}
}
