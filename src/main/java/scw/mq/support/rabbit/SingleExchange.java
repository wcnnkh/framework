package scw.mq.support.rabbit;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import scw.beans.annotation.AsyncComplete;
import scw.beans.async.DefaultAsyncCompleteService;
import scw.core.Consumer;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.serializer.NoTypeSpecifiedSerializer;
import scw.mq.amqp.AmqpQueueConfig;
import scw.mq.amqp.Exchange;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public class SingleExchange<T> implements Exchange<T> {
	protected static Logger logger = LoggerFactory.getLogger(SingleExchange.class);
	private final SingleExchangeChannelFactory channelFactory;
	private final NoTypeSpecifiedSerializer serializer;
	private final boolean autoErrorAppend;
	// 是否使用异步确认
	private final boolean asyncComplete;

	public SingleExchange(SingleExchangeChannelFactory channelFactory, NoTypeSpecifiedSerializer serializer,
			boolean autoErrorAppend, boolean asyncComplete) throws IOException, TimeoutException {
		this.channelFactory = channelFactory;
		this.serializer = serializer;
		this.autoErrorAppend = autoErrorAppend;
		this.asyncComplete = asyncComplete;
	}

	public final SingleExchangeChannelFactory getChannelFactory() {
		return channelFactory;
	}

	public final NoTypeSpecifiedSerializer getSerializer() {
		return serializer;
	}

	public void bindConsumer(String routingKey, String queueName, boolean durable, boolean exclusive,
			boolean autoDelete, Map<String, Object> arguments, Consumer<T> consumer) {
		Channel channel = channelFactory.getChannel(routingKey);
		try {
			channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
			channel.queueBind(queueName, channelFactory.getExchange(), routingKey, arguments);
			channel.basicConsume(queueName, autoDelete,
					new RabbitDefaultConsumer(channel, autoDelete, consumer, queueName, autoErrorAppend));
		} catch (IOException e) {
			logger.error("bind：exchange={},rotingKey={},durable={},exclusive={},autoDelete={}",
					channelFactory.getExchangeType(), routingKey, durable, exclusive, autoDelete);
			throw new RuntimeException(e);
		}
	}

	public void bindConsumer(String routingKey, String queueName, boolean durable, boolean exclusive,
			boolean autoDelete, Consumer<T> consumer) {
		bindConsumer(routingKey, queueName, durable, exclusive, autoDelete, null, consumer);
	}

	private void basePush(String routingKey, boolean mandatory, boolean immediate, T message) {
		try {
			channelFactory.getChannel(routingKey).basicPublish(channelFactory.getExchange(), routingKey, mandatory,
					immediate, null, getSerializer().serialize(message));
		} catch (IOException e) {
			logger.error("push：exchange={},rotingKey={},mandatory={},immediate={}", channelFactory.getExchange(),
					routingKey, mandatory, immediate);
			throw new RuntimeException(e);
		}
	}

	@AsyncComplete(service=DefaultAsyncCompleteService.class)
	public void asyncPush(String routingKey, boolean mandatory, boolean immediate, T message) {
		basePush(routingKey, mandatory, immediate, message);
	}

	private void basePush(String routingKey, T message) {
		try {
			channelFactory.getChannel(routingKey).basicPublish(channelFactory.getExchange(), routingKey, null,
					getSerializer().serialize(message));
		} catch (IOException e) {
			logger.error("push：exchange={},rotingKey={}", channelFactory.getExchange(), routingKey);
			throw new RuntimeException(e);
		}
	}

	@AsyncComplete(service=DefaultAsyncCompleteService.class)
	public void asyncPush(String routingKey, T message) {
		basePush(routingKey, message);
	}

	public void push(final String routingKey, final boolean mandatory, final boolean immediate, final T message) {
		if (TransactionManager.hasTransaction()) {
			TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
				@Override
				public void afterProcess() {
					if (asyncComplete) {
						asyncPush(routingKey, mandatory, immediate, message);
					} else {
						basePush(routingKey, mandatory, immediate, message);
					}
				}
			});
		} else {
			if (asyncComplete) {
				asyncPush(routingKey, mandatory, immediate, message);
			} else {
				basePush(routingKey, mandatory, immediate, message);
			}
		}
	}

	public void push(final String routingKey, final T message) {
		if (TransactionManager.hasTransaction()) {
			TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
				@Override
				public void afterProcess() {
					if (asyncComplete) {
						asyncPush(routingKey, message);
					} else {
						basePush(routingKey, message);
					}
				}
			});
		} else {
			if (asyncComplete) {
				asyncPush(routingKey, message);
			} else {
				basePush(routingKey, message);
			}
		}
	}

	final class RabbitDefaultConsumer extends DefaultConsumer {
		private final Consumer<T> consumer;
		private final boolean autoAck;
		private final String name;
		private final boolean autoErrorAppend;// 如果异常，自动添加到队列尾部

		public RabbitDefaultConsumer(Channel channel, boolean autoAck, Consumer<T> consumer, String name,
				boolean autoErrorAppend) {
			super(channel);
			this.consumer = consumer;
			this.autoAck = autoAck;
			this.name = name;
			this.autoErrorAppend = autoErrorAppend;
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
				throws IOException {
			if (body == null) {
				return;
			}

			T message = null;
			try {
				message = getSerializer().deserialize(body);
				consumer.consume(message);
				if (!autoAck) {
					getChannel().basicAck(envelope.getDeliveryTag(), false);
				}
			} catch (Throwable e) {
				logger.error(e, "消费者异常, exchange={}, routingKey={}, queueName={}", envelope.getExchange(),
						envelope.getRoutingKey(), name);
				if (autoErrorAppend) {
					push(envelope.getRoutingKey(), message);
					if (!autoAck) {
						getChannel().basicAck(envelope.getDeliveryTag(), false);
					}
				}
			}
		}
	}

	public void bindConsumer(String routingKey, String queueName, Consumer<T> consumer) {
		bindConsumer(routingKey, queueName, true, false, false, null, consumer);
	}

	public void bindConsumer(AmqpQueueConfig config, Consumer<T> consumer) {
		bindConsumer(config.getRoutingKey(), config.getQueueName(), config.isDurable(), config.isExclusive(),
				config.isAutoDelete(), null, consumer);
	}
}
