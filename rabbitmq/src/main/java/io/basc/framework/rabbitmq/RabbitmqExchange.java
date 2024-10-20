package io.basc.framework.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import io.basc.framework.amqp.ExchangeDeclare;
import io.basc.framework.amqp.QueueDeclare;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public class RabbitmqExchange extends AbstractRabbitmqExchange {
	private static final String DIX_PREFIX = "io.basc.framework.dix.";
	private static final String DELAY_PREFIX = "io.basc.framework.delay.";
	private static final String DELAY_EXCHANGE_TYPE = "fanout";

	private final ExchangeDeclare delayExchangeDeclare;
	private final QueueDeclare delayQueueDeclare;
	private final Connection connection;
	private final ExchangeDeclare dixExchangeDeclare;
	private final QueueDeclare dixQueueDeclare;

	public RabbitmqExchange(Connection connection, ExchangeDeclare exchangeDeclare) {
		super(exchangeDeclare);
		this.connection = connection;
		checkName(exchangeDeclare.getName());

		// 延迟消息路由
		this.delayExchangeDeclare = new ExchangeDeclare(DELAY_PREFIX + getExchangeDeclare().getName());
		delayExchangeDeclare.setType(DELAY_EXCHANGE_TYPE);

		this.dixExchangeDeclare = new ExchangeDeclare(DIX_PREFIX + getExchangeDeclare().getName());
		this.dixExchangeDeclare.setType(DELAY_EXCHANGE_TYPE);
		this.dixQueueDeclare = new QueueDeclare(dixExchangeDeclare.getName() + ".queue");

		this.delayQueueDeclare = new QueueDeclare(delayExchangeDeclare.getName() + ".queue");
		// 设置延迟消息的死信队列(注意:
		// 不要给此队列绑定消费者，这样在消息过期后会进入死信队列，在死信队列中将消息发给指定队列那么就实现了消息的延迟发送)
		this.delayQueueDeclare.setArgument(X_DEAD_LETTER_EXCHANGE, dixExchangeDeclare.getName());
	}

	protected void checkName(String name) {
		Assert.requiredArgument(
				StringUtils.isNotEmpty(name) && !name.startsWith(DIX_ROUTING_KEY) && !name.startsWith(DELAY_ROUTING_KEY)
						&& !name.startsWith(DIX_PREFIX) && !name.startsWith(DELAY_PREFIX),
				name);
	}

	@Override
	protected Channel createChannel() throws IOException {
		return connection.createChannel();
	}

	@Override
	public ExchangeDeclare getDixExchangeDeclare() {
		return dixExchangeDeclare;
	}

	@Override
	public QueueDeclare getDixQueueDeclare() {
		return dixQueueDeclare;
	}

	@Override
	public ExchangeDeclare getDelayExchangeDeclare() {
		return delayExchangeDeclare;
	}

	@Override
	public QueueDeclare getDelayQueueDeclare() {
		return delayQueueDeclare;
	}
}
