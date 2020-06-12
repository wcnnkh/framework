package scw.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import scw.amqp.AbstractExchange;
import scw.amqp.ExchangeDeclare;
import scw.amqp.MessageListener;
import scw.amqp.QueueDeclare;
import scw.compatible.CompatibleUtils;
import scw.core.Assert;
import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.io.serialzer.NoTypeSpecifiedSerializer;
import scw.json.JSONUtils;

public class RabbitmqExchange extends AbstractExchange {
	private static final String DIX_PREFIX = "scw.dix.";
	private static final String DELAY_PREFIX = "scw.delay.";
	private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";

	private final ThreadLocal<Channel> channelThreadLocal = new ThreadLocal<Channel>();
	private final ExchangeDeclare exchangeDeclare;
	private final ExchangeDeclare dixExchangeDeclare;
	private final ExchangeDeclare delayExchangeDeclare;
	private final QueueDeclare dixQueueDeclare;
	private final QueueDeclare delayQueueDeclare;
	private final Connection connection;
	
	private void checkName(String name) {
		Assert.requiredArgument(
				StringUtils.isNotEmpty(name) && !name.startsWith(DIX_PREFIX) && !name.startsWith(DELAY_PREFIX), name);
	}

	public RabbitmqExchange(NoTypeSpecifiedSerializer serializer, Connection connection,
			ExchangeDeclare exchangeDeclare) throws IOException {
		super(serializer, exchangeDeclare);
		this.connection = connection;
		checkName(exchangeDeclare.getName());
		// 死信路由
		this.dixExchangeDeclare = new ExchangeDeclare(DIX_PREFIX + exchangeDeclare.getName());

		// 死信队列
		this.dixQueueDeclare = new QueueDeclare(dixExchangeDeclare.getName() + ".queue");

		// 声明死信路由和队列
		declare(dixExchangeDeclare, dixQueueDeclare);

		// 延迟消息路由
		this.delayExchangeDeclare = new ExchangeDeclare(DELAY_PREFIX + exchangeDeclare.getName());

		// 延迟消息队列
		this.delayQueueDeclare = new QueueDeclare(delayExchangeDeclare.getName() + ".queue");
		// 设置延迟消息的死信队列(注意:
		// 不要给此队列绑定消费者，这样在消息过期后会进入死信队列，在死信队列中将消息发给指定队列那么就实现了消息的延迟发送)
		delayQueueDeclare.setArgument(X_DEAD_LETTER_EXCHANGE, dixExchangeDeclare.getName());

		// 声明延迟消息路由和队列
		declare(exchangeDeclare, delayQueueDeclare);

		this.exchangeDeclare = exchangeDeclare;
	}

	private Channel getChannel() throws IOException {
		Channel channel = channelThreadLocal.get();
		if (channel != null && channel.isOpen()) {
			return channel;
		}

		channel = connection.createChannel();
		channelThreadLocal.set(channel);
		return channel;
	}

	private void declare(ExchangeDeclare exchangeDeclare, QueueDeclare queueDeclare) throws IOException {
		if (exchangeDeclare != null) {
			getChannel().exchangeDeclare(exchangeDeclare.getName(), exchangeDeclare.getType(),
					exchangeDeclare.isDurable(), exchangeDeclare.isAutoDelete(), exchangeDeclare.isInternal(),
					exchangeDeclare.getArguments());
		}
		if (queueDeclare != null) {
			getChannel().queueDeclare(queueDeclare.getName(), queueDeclare.isDurable(), queueDeclare.isExclusive(),
					queueDeclare.isAutoDelete(), queueDeclare.getArguments());
		}
	}

	private void queueBind(ExchangeDeclare exchangeDeclare, QueueDeclare queueDeclare, String routingKey,
			MessageListener messageListener) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("bind exchangeDeclare:{}, queueDeclare:{}, routingKey:{}", exchangeDeclare, queueDeclare,
					routingKey);
		}

		declare(exchangeDeclare, queueDeclare);
		getChannel().queueBind(queueDeclare.getName(), exchangeDeclare.getName(), routingKey);
		if (messageListener != null) {
			getChannel().basicConsume(queueDeclare.getName(), false,
					new RabbitmqMessageListener(getChannel(), messageListener, isMultiple()));
		}
	}

	@Override
	protected void bindInternal(String routingKey, QueueDeclare queueDeclare, MessageListener messageListener)
			throws IOException {
		checkName(queueDeclare.getName());
		queueBind(dixExchangeDeclare, dixQueueDeclare, routingKey, messageListener);
		// 不要给延迟队列绑定消费者
		queueBind(delayExchangeDeclare, delayQueueDeclare, routingKey, null);

		queueBind(exchangeDeclare, queueDeclare, routingKey, messageListener);
	}
	
	@Override
	protected void basicPublish(MessageLog message) throws IOException {
		ExchangeDeclare exchangeDeclare = message.getMessageProperties().getDelay() > 0 ? this.delayExchangeDeclare
				: this.exchangeDeclare;
		if (logger.isDebugEnabled()) {
			logger.debug("push exchange={}, routingKey={}, properties={}, body={}", exchangeDeclare.getName(),
					message.getRoutingKey(), JSONUtils.toJSONString(message.getMessageProperties()),
					CompatibleUtils.getStringOperations().createString(message.getBody(), Constants.DEFAULT_CHARSET));
		}

		if (message.getMessageProperties().getDeliveryMode() == null) {
			message.getMessageProperties().setDeliveryMode(2);// 消息持久化
		}

		Channel channel = getChannel();
		channel.basicPublish(exchangeDeclare.getName(), message.getRoutingKey(), RabbitmqUitls.toBasicProperties(message.getMessageProperties()),
				message.getBody());
	}

	protected boolean isMultiple() {
		return false;
	}
}
