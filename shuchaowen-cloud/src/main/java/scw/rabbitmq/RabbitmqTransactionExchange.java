package scw.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import scw.amqp.ExchangeDeclare;
import scw.amqp.MessageListener;
import scw.amqp.MessageProperties;
import scw.amqp.QueueDeclare;
import scw.amqp.support.TransactionExchange;
import scw.complete.CompleteService;
import scw.core.Assert;
import scw.core.utils.StringUtils;
import scw.io.serialzer.NoTypeSpecifiedSerializer;
import scw.io.serialzer.SerializerUtils;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class RabbitmqTransactionExchange extends TransactionExchange {
	private static final String DIX_PREFIX = "scw.dix.";
	private static final String DELAY_PREFIX = "scw.delay.";
	private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";

	private static Logger logger = LoggerUtils.getLogger(RabbitmqTransactionExchange.class);
	private ThreadLocal<Channel> channelThreadLocal;
	private ExchangeDeclare exchangeDeclare;
	private ExchangeDeclare dixExchangeDeclare;
	private ExchangeDeclare delayExchangeDeclare;
	private Connection connection;

	private void checkName(String name) {
		Assert.requiredArgument(
				StringUtils.isNotEmpty(name) && !name.startsWith(DIX_PREFIX) && !name.startsWith(DELAY_PREFIX), name);
	}

	public RabbitmqTransactionExchange(NoTypeSpecifiedSerializer serializer, final Connection connection,
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
			channel.basicConsume(queueDeclare.getName(), false,
					new RabbitmqMessageListener(channel, messageListener, isMultiple()));
		}
	}

	protected boolean isRequeue() {
		return false;
	}
	
	@Override
	protected void bindInternal(String routingKey, QueueDeclare queueDeclare, MessageListener messageListener) {
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

	protected void basePush(String routingKey, MessageProperties messageProperties, byte[] body) {
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
		if (logger.isTraceEnabled()) {
			logger.trace("push routingKey={}, properties={}, body={}", routingKey,
					JSONUtils.toJSONString(messageProperties), body);
		}

		if (messageProperties.getDelay() > 0) {
			channel.basicPublish(delayExchangeDeclare.getName(), routingKey,
					RabbitmqUitls.toBasicProperties(messageProperties), body);
			return;
		}
		channel.basicPublish(exchangeDeclare.getName(), routingKey, RabbitmqUitls.toBasicProperties(messageProperties),
				body);
	}

	protected boolean isMultiple() {
		return false;
	}
}
