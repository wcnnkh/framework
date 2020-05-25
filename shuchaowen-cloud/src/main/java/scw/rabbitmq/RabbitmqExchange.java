package scw.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import scw.amqp.ExchangeDeclare;
import scw.amqp.Message;
import scw.amqp.MessageListener;
import scw.amqp.MessageProperties;
import scw.amqp.QueueDeclare;
import scw.amqp.support.TransactionPushExchange;
import scw.complete.CompleteService;
import scw.core.Assert;
import scw.core.utils.StringUtils;
import scw.io.serialzer.NoTypeSpecifiedSerializer;
import scw.io.serialzer.SerializerUtils;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class RabbitmqExchange extends TransactionPushExchange {
	private static final String DIX_PREFIX = "scw.dix.";
	private static final String DELAY_PREFIX = "scw.delay.";
	private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";

	private static Logger logger = LoggerUtils.getLogger(RabbitmqExchange.class);
	private ThreadLocal<Channel> channelThreadLocal;
	private ExchangeDeclare exchangeDeclare;
	private ExchangeDeclare dixExchangeDeclare;
	private ExchangeDeclare delayExchangeDeclare;
	private Connection connection;

	private void checkName(String name) {
		Assert.requiredArgument(
				StringUtils.isNotEmpty(name) && !name.startsWith(DIX_PREFIX) && !name.startsWith(DELAY_PREFIX), name);
	}

	public RabbitmqExchange(NoTypeSpecifiedSerializer serializer, final Connection connection,
			ExchangeDeclare exchangeDeclare, CompleteService completeService, String beanId) throws IOException {
		super(serializer, completeService, beanId);
		checkName(exchangeDeclare.getName());
		this.dixExchangeDeclare = SerializerUtils.clone(exchangeDeclare)
				.setName(DIX_PREFIX + exchangeDeclare.getName());

		this.delayExchangeDeclare = SerializerUtils.clone(exchangeDeclare)
				.setName(DELAY_PREFIX + exchangeDeclare.getName());
		this.exchangeDeclare = SerializerUtils.clone(exchangeDeclare);

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
		this.connection = connection;
	}

	private void declare(Channel channel, ExchangeDeclare exchangeDeclare, QueueDeclare queueDeclare)
			throws IOException {
		channel.exchangeDeclare(exchangeDeclare.getName(), exchangeDeclare.getType(), exchangeDeclare.isDurable(),
				exchangeDeclare.isAutoDelete(), exchangeDeclare.isInternal(), exchangeDeclare.getArguments());

		channel.queueDeclare(queueDeclare.getName(), queueDeclare.isDurable(), queueDeclare.isExclusive(),
				queueDeclare.isAutoDelete(), queueDeclare.getArguments());
	}

	private void queueBind(ExchangeDeclare exchangeDeclare, QueueDeclare queueDeclare, String routingKey,
			MessageListener messageListener) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("bind exchangeDeclare:{}, queueDeclare:{}, routingKey:{}", exchangeDeclare, queueDeclare,
					routingKey);
		}
		Channel channel = connection.createChannel();
		declare(channel, exchangeDeclare, queueDeclare);
		channel.queueBind(queueDeclare.getName(), exchangeDeclare.getName(), routingKey);
		if (messageListener != null) {
			channel.basicConsume(queueDeclare.getName(), false, new ConsumerInternal(channel, messageListener));
		}
	}

	protected boolean isRequeue() {
		return false;
	}

	public void bind(String routingKey, QueueDeclare queueDeclare, MessageListener messageListener) {
		checkName(queueDeclare.getName());
		try {
			QueueDeclare dixQueueDeclare = SerializerUtils.clone(queueDeclare)
					.setName(DIX_PREFIX + queueDeclare.getName());
			queueBind(dixExchangeDeclare, dixQueueDeclare, routingKey, messageListener);

			queueDeclare.setArgument(X_DEAD_LETTER_EXCHANGE, dixExchangeDeclare.getName());
			queueBind(exchangeDeclare, queueDeclare, routingKey, messageListener);

			QueueDeclare delayQueueDeclare = SerializerUtils.clone(queueDeclare)
					.setName(DELAY_PREFIX + queueDeclare.getName());
			queueBind(delayExchangeDeclare, delayQueueDeclare, routingKey, null);
		} catch (IOException e) {
			logger.error(e, "bind error, Try again in 10 seconds");
			try {
				Thread.sleep(10000);
				bind(routingKey, queueDeclare, messageListener);
			} catch (InterruptedException e1) {
			}
		}
	}

	protected final void basePush(String routingKey, MessageProperties messageProperties, byte[] body) {
		Channel channel = channelThreadLocal.get();
		if (channel == null) {
			throw new RuntimeException("Unable to get rabbitmq channel");
		}
		try {
			push(channel, routingKey, messageProperties, body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void push(Channel channel, String routingKey, MessageProperties messageProperties, byte[] body)
			throws IOException {
		if (messageProperties.getDelay() > 0) {
			channel.basicPublish(delayExchangeDeclare.getName(), routingKey, toBasicProperties(messageProperties),
					body);
			return;
		}
		channel.basicPublish(exchangeDeclare.getName(), routingKey, toBasicProperties(messageProperties), body);
	}

	protected BasicProperties toBasicProperties(MessageProperties messageProperties) {
		return new BasicProperties().builder().appId(messageProperties.getAppId())
				.clusterId(messageProperties.getClusterId()).contentEncoding(messageProperties.getContentEncoding())
				.contentType(messageProperties.getContentType()).correlationId(messageProperties.getCorrelationId())
				.deliveryMode(messageProperties.getDeliveryMode())
				.expiration(messageProperties.getExpiration() == null ? null : ("" + messageProperties.getExpiration()))
				.headers(messageProperties.getHeaders()).messageId(messageProperties.getMessageId())
				.priority(messageProperties.getPriority()).replyTo(messageProperties.getReplyTo())
				.timestamp(messageProperties.getTimestamp()).type(messageProperties.getType())
				.userId(messageProperties.getUserId()).build();
	}

	protected Message toMessage(com.rabbitmq.client.AMQP.BasicProperties basicProperties, byte[] body) {
		Message message = new Message(body);
		message.setAppId(basicProperties.getAppId());
		message.setClusterId(basicProperties.getClusterId());
		message.setContentEncoding(basicProperties.getContentEncoding());
		message.setContentType(basicProperties.getContentType());
		message.setCorrelationId(basicProperties.getCorrelationId());
		message.setDeliveryMode(basicProperties.getDeliveryMode());
		message.setExpiration(basicProperties.getExpiration());
		message.setHeaders(basicProperties.getHeaders());
		message.setMessageId(basicProperties.getMessageId());
		message.setPriority(basicProperties.getPriority());
		message.setReplyTo(basicProperties.getReplyTo());
		message.setTimestamp(basicProperties.getTimestamp());
		message.setType(basicProperties.getType());
		message.setUserId(basicProperties.getUserId());
		return message;
	}

	protected boolean isMultiple() {
		return false;
	}

	private final class ConsumerInternal extends DefaultConsumer {
		private final MessageListener messageListener;

		public ConsumerInternal(Channel channel, MessageListener messageListener) {
			super(channel);
			this.messageListener = messageListener;
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
				throws IOException {
			Message message = toMessage(properties, body);
			if (logger.isTraceEnabled()) {
				logger.trace("handleDelivery: {}", JSONUtils.toJSONString(message));
			}

			if (message.getDelay() > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("delay message forward properties: {}", JSONUtils.toJSONString(message));
				}

				message.setDelay(0, TimeUnit.SECONDS);
				push(getChannel(), envelope.getRoutingKey(), message, message.getBody());
				getChannel().basicAck(envelope.getDeliveryTag(), isMultiple());
				return;
			}

			onMessageInternal(envelope.getExchange(), envelope.getRoutingKey(), message, messageListener);
			getChannel().basicAck(envelope.getDeliveryTag(), isMultiple());
		}
	}
}
