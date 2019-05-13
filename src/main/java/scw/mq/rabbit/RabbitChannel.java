package scw.mq.rabbit;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.serializer.NoTypeSpecifiedSerializer;

public final class RabbitChannel<T> {
	private static Logger logger = LoggerFactory.getLogger(RabbitChannel.class);

	private final Channel channel;
	private final String exchange;
	private final String routingKey;
	private final NoTypeSpecifiedSerializer serializer;

	public RabbitChannel(Channel channel, String exchange, String routingKey, NoTypeSpecifiedSerializer serializer) {
		this.exchange = exchange;
		this.channel = channel;
		this.routingKey = routingKey;
		this.serializer = serializer;
	}

	public void push(T message) throws IOException {
		channel.basicPublish(exchange, routingKey, null, serializer.serialize(message));
	}

	public void push(BasicProperties props, T message) throws IOException {
		channel.basicPublish(exchange, routingKey, props, serializer.serialize(message));
	}

	public void push(boolean mandatory, boolean immediate, BasicProperties props, T message) throws IOException {
		channel.basicPublish(exchange, routingKey, mandatory, immediate, props, serializer.serialize(message));
	}

	public void addConsumer(final RabbitConsumerDefinition<T> consumer) throws IOException {
		channel.queueDeclare(consumer.getQueueName(), consumer.isDurable(), consumer.isExclusive(),
				consumer.isAutoDelete(), consumer.getArguments());
		;
		channel.queueBind(consumer.getQueueName(), exchange, routingKey);

		channel.basicConsume(consumer.getQueueName(), consumer.isAutoDelete(), new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
					throws IOException {
				if (body == null) {
					return;
				}

				T message;
				try {
					message = serializer.deserialize(body);
					consumer.consumer(properties, message);
					if (!consumer.isAutoDelete()) {
						channel.basicAck(envelope.getDeliveryTag(), false);
					}
				} catch (Throwable e) {
					logger.error("消费者异常, exchange=" + exchange + ", routingKey=" + routingKey + ", queueName="
							+ consumer.getQueueName(), e);
				}
			}
		});
	}
}
