package scw.mq.rabbit;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import scw.beans.annotation.AsyncComplete;
import scw.core.Consumer;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.serializer.support.JavaSerializer;
import scw.core.utils.StringUtils;
import scw.mq.MQ;

public abstract class AbstractRabbitMQ<T> extends ChannelFactory implements MQ<T> {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public AbstractRabbitMQ(ConnectionFactory connectionFactory, String exchange, String exchangeType)
			throws IOException, TimeoutException {
		super(connectionFactory, exchange, exchangeType);
	}

	protected abstract String getRoutingKey(String name);

	protected abstract BasicProperties getBasicProperties(String name);

	/**
	 * 当mandatory参数设为true时， 交换器无法根据自身的类型和路由键找到一个符合条件的队列，
	 * 那么RabbitMQ会调用Basic.Return命令将消息返回给生产者。
	 * 当mandatory参数设置为false时，出现上述情形，则消息直接被丢弃。
	 * 
	 * @param name
	 * @return
	 */
	protected abstract boolean isMandatory(String name);

	protected abstract boolean isImmediate(String name);

	@AsyncComplete
	public void push(String name, T message) {
		if (StringUtils.isEmpty(name) || message == null) {
			logger.error("队列名称或消费内容为空，队列名称：{}", name);
			return;
		}

		Channel channel = getChannel(name);
		if (channel == null) {
			logger.error("无法获取channel，队列名称：{}", name);
			return;
		}

		try {
			channel.basicPublish(getExchange(), getRoutingKey(name), isMandatory(name), isImmediate(name),
					getBasicProperties(name), JavaSerializer.SERIALIZER.serialize(message));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract boolean isDurable(String name);

	/**
	 * 队列的排他性的理解，是针对首次建立连接的，一个连接下面多个通道也是可见的， 对于其他连接是不可见的
	 * 
	 * 设置队列是否排他，为true时，则设置队列排他，改队列对首次声明他的队列连接可见。
	 * 
	 * 排他队列是基于连接可见的，同一个连接的不同信道是可以同时访问同一连接创建的。
	 * 
	 * RabbitMQ会自动删除这个队列，而不管这个队列是否被声明成持久性的（Durable =true)。
	 * 也就是说即使客户端程序将一个排他性的队列声明成了Durable的，
	 * 只要调用了连接的Close方法或者客户端程序退出了，RabbitMQ都会删除这个队列。
	 * 注意这里是连接断开的时候，而不是通道断开。这个其实前一点保持一致，只区别连接而非通道; * @param name
	 * 
	 * @return
	 */
	protected abstract boolean isExclusive(String name);

	protected abstract boolean isAutoDelete(String name);

	protected abstract Map<String, Object> getArguments(String name);

	public void addConsumer(String name, Consumer<T> consumer) {
		if (StringUtils.isEmpty(name) || consumer == null) {
			logger.error("队列名称或消费者为空，队列名称：{}", name);
			return;
		}

		Channel channel = getChannel(name);
		if (channel == null) {
			logger.error("无法获取channel，队列名称：{}", name);
			return;
		}

		try {
			channel.queueDeclare(name, isDurable(name), isExclusive(name), isAutoDelete(name), getArguments(name));
			channel.queueBind(name, getExchange(), getRoutingKey(name));
			channel.basicConsume(name, isAutoDelete(name),
					new RabbitDefaultConsumer(channel, isAutoDelete(name), consumer, name));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	final class RabbitDefaultConsumer extends DefaultConsumer {
		private final Consumer<T> consumer;
		private final boolean autoAck;
		private final String name;

		public RabbitDefaultConsumer(Channel channel, boolean autoAck, Consumer<T> consumer, String name) {
			super(channel);
			this.consumer = consumer;
			this.autoAck = autoAck;
			this.name = name;
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
				throws IOException {
			if (body == null) {
				return;
			}

			T message;
			try {
				message = JavaSerializer.SERIALIZER.deserialize(body);
				consumer.consume(message);
				if (!autoAck) {
					getChannel().basicAck(envelope.getDeliveryTag(), false);
				}
			} catch (Throwable e) {
				logger.error("消费者异常, exchange=" + getExchange() + ", routingKey=" + getRoutingKey(name) + ", queueName="
						+ name, e);
			}
		}
	}
}
