package io.basc.framework.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;

import io.basc.framework.amqp.AbstractExchange;
import io.basc.framework.amqp.BinaryExchange;
import io.basc.framework.amqp.ExchangeDeclare;
import io.basc.framework.amqp.ExchangeException;
import io.basc.framework.amqp.Message;
import io.basc.framework.amqp.MessageListener;
import io.basc.framework.amqp.QueueDeclare;
import io.basc.framework.beans.factory.Init;
import io.basc.framework.lang.NamedThreadLocal;
import io.basc.framework.register.LimitedRegistration;
import io.basc.framework.register.Registration;
import io.basc.framework.register.RegistrationException;

public abstract class AbstractRabbitmqExchange extends AbstractExchange<byte[]> implements BinaryExchange, Init {
	static final String DIX_ROUTING_KEY = "io.basc.framework.dix.routingKey";
	static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
	static final String DELAY_ROUTING_KEY = "io.basc.framework.delay.routingKey";
	private final ThreadLocal<Channel> channelThreadLocal = new NamedThreadLocal<Channel>(
			RabbitmqExchange.class.getSimpleName() + "-channel");

	/**
	 * 默认持久化
	 */
	private int defaultDeliveryMode = 2;

	public AbstractRabbitmqExchange(ExchangeDeclare exchangeDeclare) {
		super(exchangeDeclare);
		checkName(exchangeDeclare.getName());
	}

	protected abstract void checkName(String name);

	public abstract ExchangeDeclare getDixExchangeDeclare();

	public abstract QueueDeclare getDixQueueDeclare();

	public abstract ExchangeDeclare getDelayExchangeDeclare();

	public abstract QueueDeclare getDelayQueueDeclare();

	public void init() {
		// 声明路由

		try {
			declare(getExchangeDeclare(), null);

			// 声明死信路由和队列
			declare(getDixExchangeDeclare(), getDixQueueDeclare());
			queueBind(getDixExchangeDeclare(), getDixQueueDeclare(), DIX_ROUTING_KEY,
					new MessageListenerInternal(null));
		} catch (IOException e) {
			throw new RuntimeException("dix", e);
		}

		// 延迟消息队列
		QueueDeclare delayQueueDeclare = getDelayQueueDeclare();
		// 设置延迟消息的死信队列(注意:
		// 不要给此队列绑定消费者，这样在消息过期后会进入死信队列，在死信队列中将消息发给指定队列那么就实现了消息的延迟发送)
		delayQueueDeclare.setArgument(X_DEAD_LETTER_EXCHANGE, getDixExchangeDeclare().getName());

		// 声明延迟消息路由和队列
		try {
			declare(getDelayExchangeDeclare(), delayQueueDeclare);
			queueBind(getDelayExchangeDeclare(), delayQueueDeclare, DELAY_ROUTING_KEY, null);
		} catch (Exception e) {
			throw new RuntimeException("delay", e);
		}
	}

	protected abstract Channel createChannel() throws IOException;

	public Channel getChannel() throws IOException {
		Channel channel = channelThreadLocal.get();
		if (channel != null && channel.isOpen()) {
			return channel;
		}

		channel = createChannel();
		channelThreadLocal.set(channel);
		return channel;
	}

	private final void declare(ExchangeDeclare exchangeDeclare, QueueDeclare queueDeclare) throws IOException {
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

	private final Registration queueBind(ExchangeDeclare exchangeDeclare, QueueDeclare queueDeclare, String routingKey,
			MessageListener<byte[]> messageListener) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("bind exchangeDeclare:{}, queueDeclare:{}, routingKey:{}", exchangeDeclare, queueDeclare,
					routingKey);
		}

		Channel channel = createChannel();
		channel.queueBind(queueDeclare.getName(), exchangeDeclare.getName(), routingKey);
		if (messageListener != null) {
			channel.basicConsume(queueDeclare.getName(), false,
					new RabbitmqMessageListener(channel, messageListener, isMultiple()));
		}

		return LimitedRegistration.of(() -> {
			try {
				logger.info("unbind exchangeDeclare:{}, queueDeclare:{}, routingKey:{}, result:{}", exchangeDeclare,
						queueDeclare, routingKey);
				channel.close();
			} catch (IOException | TimeoutException e) {
				throw new RegistrationException("unbind exchangeDeclare:" + exchangeDeclare + ", queueDeclare:"
						+ queueDeclare + ", routingKey:" + routingKey, e);
			}
		});
	}

	@Override
	protected Registration bindInternal(String routingKey, QueueDeclare queueDeclare,
			MessageListener<byte[]> messageListener) throws IOException {
		checkName(queueDeclare.getName());
		declare(null, queueDeclare);
		return queueBind(getExchangeDeclare(), queueDeclare, routingKey, messageListener);
	}

	public int getDefaultDeliveryMode() {
		return defaultDeliveryMode;
	}

	public void setDefaultDeliveryMode(int defaultDeliveryMode) {
		this.defaultDeliveryMode = defaultDeliveryMode;
	}

	@Override
	public void basicPublish(String routingKey, Message<byte[]> message) throws ExchangeException {
		ExchangeDeclare exchangeDeclare = message.getDelay() > 0 ? getDelayExchangeDeclare() : getExchangeDeclare();
		if (logger.isDebugEnabled()) {
			logger.debug("push exchange={}, routingKey={}, properties={}, body={}", exchangeDeclare.getName(),
					routingKey, message);
		}

		if (message.getDeliveryMode() == null) {
			message.setDeliveryMode(defaultDeliveryMode);// 消息持久化
		}

		Channel channel;
		try {
			channel = getChannel();
			channel.basicPublish(exchangeDeclare.getName(), routingKey, RabbitmqUitls.toBasicProperties(message),
					message.getBody());
		} catch (IOException e) {
			throw new ExchangeException(e);
		}
	}

	protected boolean isMultiple() {
		return false;
	}
}
