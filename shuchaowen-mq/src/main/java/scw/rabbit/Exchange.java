package scw.rabbit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import scw.core.Assert;
import scw.core.utils.StringUtils;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Exchange {
	private static final String DIX_PREFIX = "scw.dix.";
	private static final String DELAY_PREFIX = "scw.delay.";

	private static Logger logger = LoggerUtils.getLogger(Exchange.class);
	private ThreadLocal<Channel> channelThreadLocal;
	private String exchangeName;
	private Connection connection;
	private String dixExchangeName;
	private String delayExchangeName;

	private void checkName(String name) {
		Assert.requiredArgument(
				StringUtils.isNotEmpty(name)
						&& !name.startsWith(DIX_PREFIX)
						&& !name.startsWith(DELAY_PREFIX), name);
	}

	public Exchange(final Connection connection, String exchangeName,
			BuiltinExchangeType exchangeType) throws IOException,
			TimeoutException {
		checkName(exchangeName);

		channelThreadLocal = new ThreadLocal<Channel>() {
			@Override
			protected Channel initialValue() {
				try {
					return connection.createChannel();
				} catch (IOException e) {
					logger.error(e, "create channel error");
					return null;
				}
			}
		};
		this.exchangeName = exchangeName;
		this.connection = connection;
		this.dixExchangeName = DIX_PREFIX + exchangeName;
		this.delayExchangeName = DELAY_PREFIX + exchangeName;

		exchangeDeclare(exchangeName, exchangeType);
		exchangeDeclare(dixExchangeName, exchangeType);
		exchangeDeclare(delayExchangeName, exchangeType);
	}

	private void exchangeDeclare(String exchangeName,
			BuiltinExchangeType exchangeType) throws IOException {
		Channel channel = connection.createChannel();
		channel.exchangeDelete(exchangeName);
		channel.exchangeDeclare(exchangeName, exchangeType, true);
	}

	private void queueDeclare(String queueName, String exchangeName,
			String routingKey, boolean durable, boolean exclusive,
			boolean autoDelete, Map<String, Object> params, Consumer consumer)
			throws IOException {
		Channel channel = connection.createChannel();
		channel.queueDelete(queueName);
		channel.queueDeclare(queueName, durable, exclusive, autoDelete, params);
		channel.queueBind(queueName, exchangeName, routingKey);
		if (consumer != null) {
			channel.basicConsume(queueName, false, new ConsumerInternal(
					channel, consumer));
		}
	}

	protected boolean isRequeue() {
		return false;
	}

	public void bindConsumer(String routingKey, String queueName,
			Consumer consumer) throws IOException {
		bindConsumer(routingKey, queueName, true, false, false, null, consumer);
	}

	public void bindConsumer(String routingKey, String queueName,
			boolean durable, boolean exclusive, boolean autoDelete,
			Consumer consumer) throws IOException {
		bindConsumer(routingKey, queueName, durable, exclusive, autoDelete,
				null, consumer);
	}

	public void bindConsumer(String routingKey, String queueName,
			boolean durable, boolean exclusive, boolean autoDelete,
			Map<String, Object> props, Consumer consumer) throws IOException {
		checkName(queueName);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x-dead-letter-exchange", dixExchangeName);// 死信路由就是自身
		if (props != null) {
			params.putAll(props);
		}

		queueDeclare(queueName, exchangeName, routingKey, durable, exclusive,
				autoDelete, params, consumer);
		queueDeclare("dix." + queueName, dixExchangeName, routingKey, durable,
				exclusive, autoDelete, null, consumer);
		queueDeclare("delay." + queueName, delayExchangeName, routingKey,
				durable, exclusive, autoDelete, params, null);
	}

	/**
	 * 发送消息
	 * 
	 * @param routingKey
	 * @param body
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public void push(String routingKey, Message message) throws IOException,
			TimeoutException {
		Channel channel = channelThreadLocal.get();
		if (channel == null) {
			throw new RuntimeException("Unable to get rabbitmq channel");
		}
		push(channel, routingKey, message);
	}

	public void push(Channel channel, String routingKey, Message message)
			throws IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("push: {}", JSONUtils.toJSONString(message));
		}
		if (message.getDelay() > 0) {
			channel.basicPublish(delayExchangeName, routingKey,
					message.getProperties(), message.getBody());
			return;
		}
		channel.basicPublish(exchangeName, routingKey, message.getProperties(),
				message.getBody());
	}

	protected boolean isMultiple() {
		return false;
	}

	/**
	 * 最大重试次数，-1表示永久
	 * 
	 * @return
	 */
	protected int getMaxRetryCount() {
		return -1;
	}

	protected long getRetryDelay() {
		return TimeUnit.SECONDS.toMillis(10);
	}

	private final class ConsumerInternal extends DefaultConsumer {
		private final Consumer consumer;

		public ConsumerInternal(Channel channel, Consumer consumer) {
			super(channel);
			this.consumer = consumer;
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope,
				AMQP.BasicProperties properties, byte[] body)
				throws IOException {
			Message message = new Message(body, properties);
			if (logger.isTraceEnabled()) {
				logger.trace("handleDelivery: {}",
						JSONUtils.toJSONString(message));
			}
			try {
				if (message.getDelay() > 0) {
					if (logger.isDebugEnabled()) {
						logger.debug("delay message forward properties: {}",
								JSONUtils.toJSONString(message));
					}

					message.setDelay(0, TimeUnit.SECONDS);
					push(getChannel(), envelope.getRoutingKey(), message);
					getChannel().basicAck(envelope.getDeliveryTag(),
							isMultiple());
					return;
				}

				consumer.handleDelivery(Exchange.this, consumerTag, envelope,
						message);
				getChannel().basicAck(envelope.getDeliveryTag(), isMultiple());
			} catch (Throwable e) {
				logger.error(e,
						"retry delay: {}, exchangeName={}, properties={}",
						getRetryDelay(), exchangeName,
						JSONUtils.toJSONString(properties));
				message.setDelay(getRetryDelay(), TimeUnit.MILLISECONDS);
				push(getChannel(), envelope.getRoutingKey(), message);
				getChannel().basicAck(envelope.getDeliveryTag(), isMultiple());
			}
		}
	}
}
