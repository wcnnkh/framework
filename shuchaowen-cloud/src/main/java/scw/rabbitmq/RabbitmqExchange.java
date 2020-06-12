package scw.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;

import scw.amqp.ExchangeDeclare;
import scw.amqp.MessageListener;
import scw.amqp.MessageProperties;
import scw.amqp.QueueDeclare;
import scw.amqp.support.LocalTransactionExchange;
import scw.compatible.CompatibleUtils;
import scw.complete.CompleteService;
import scw.core.Assert;
import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.io.serialzer.NoTypeSpecifiedSerializer;
import scw.json.JSONUtils;

public class RabbitmqExchange extends LocalTransactionExchange {
	private static final String DIX_PREFIX = "scw.dix.";
	private static final String DELAY_PREFIX = "scw.delay.";
	private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";

	private final ChannelFactory channelFactory;
	private final ExchangeDeclare exchangeDeclare;
	private final ExchangeDeclare dixExchangeDeclare;
	private final ExchangeDeclare delayExchangeDeclare;
	private final QueueDeclare dixQueueDeclare;
	private final QueueDeclare delayQueueDeclare;

	private void checkName(String name) {
		Assert.requiredArgument(
				StringUtils.isNotEmpty(name) && !name.startsWith(DIX_PREFIX) && !name.startsWith(DELAY_PREFIX), name);
	}

	public RabbitmqExchange(NoTypeSpecifiedSerializer serializer, ChannelFactory channelFactory,
			ExchangeDeclare exchangeDeclare, CompleteService completeService, String beanId) throws IOException {
		super(serializer, completeService, beanId);
		checkName(exchangeDeclare.getName());
		this.channelFactory = channelFactory;
		//死信路由
		this.dixExchangeDeclare = new ExchangeDeclare(DIX_PREFIX + exchangeDeclare.getName());
		
		//死信队列
		this.dixQueueDeclare = new QueueDeclare(dixExchangeDeclare.getName() + ".queue");
		
		//声明死信路由和队列
		declare(dixExchangeDeclare, dixQueueDeclare);
		
		//延迟消息路由
		this.delayExchangeDeclare = new ExchangeDeclare(DELAY_PREFIX + exchangeDeclare.getName());
		
		//延迟消息队列
		this.delayQueueDeclare = new QueueDeclare(delayExchangeDeclare.getName() + ".queue");
		//设置延迟消息的死信队列(注意: 不要给此队列绑定消费者，这样在消息过期后会进入死信队列，在死信队列中将消息发给指定队列那么就实现了消息的延迟发送)
		delayQueueDeclare.setArgument(X_DEAD_LETTER_EXCHANGE, dixExchangeDeclare.getName());

		//声明延迟消息路由和队列
		declare(exchangeDeclare, delayQueueDeclare);
		
		this.exchangeDeclare = exchangeDeclare;
	}

	private void declare(ExchangeDeclare exchangeDeclare, QueueDeclare queueDeclare)
			throws IOException {
		if(exchangeDeclare != null){
			channelFactory.getChannel().exchangeDeclare(exchangeDeclare.getName(), exchangeDeclare.getType(), exchangeDeclare.isDurable(),
					exchangeDeclare.isAutoDelete(), exchangeDeclare.isInternal(), exchangeDeclare.getArguments());
		}
		if(queueDeclare != null){
			channelFactory.getChannel().queueDeclare(queueDeclare.getName(), queueDeclare.isDurable(), queueDeclare.isExclusive(),
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
		channelFactory.getChannel().queueBind(queueDeclare.getName(), exchangeDeclare.getName(), routingKey);
		if (messageListener != null) {
			channelFactory.getChannel().basicConsume(queueDeclare.getName(), false,
					new RabbitmqMessageListener(channelFactory.getChannel(), messageListener, isMultiple()));
		}
	}

	protected boolean isRequeue() {
		return false;
	}
	
	@Override
	protected void bindInternal(String routingKey, QueueDeclare queueDeclare, MessageListener messageListener) throws IOException{
		checkName(queueDeclare.getName());
		queueBind(dixExchangeDeclare, dixQueueDeclare, routingKey,
				messageListener);
		// 不要给延迟队列绑定消费者
		queueBind(delayExchangeDeclare, delayQueueDeclare, routingKey, null);

		queueBind(exchangeDeclare, queueDeclare, routingKey, messageListener);
	}

	protected void basePush(String routingKey, MessageProperties messageProperties, byte[] body) {
		try {
			push(channelFactory.getChannel(), routingKey, messageProperties, body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void push(Channel channel, String routingKey, MessageProperties messageProperties, byte[] body)
			throws IOException {
		ExchangeDeclare exchangeDeclare = messageProperties.getDelay() > 0? this.delayExchangeDeclare:this.exchangeDeclare;
		if (logger.isDebugEnabled()) {
			logger.debug("push exchange={}, routingKey={}, properties={}, body={}", exchangeDeclare.getName(), routingKey,
					JSONUtils.toJSONString(messageProperties), CompatibleUtils.getStringOperations().createString(body, Constants.DEFAULT_CHARSET));
		}

		channel.basicPublish(exchangeDeclare.getName(), routingKey, RabbitmqUitls.toBasicProperties(messageProperties),
				body);
	}

	protected boolean isMultiple() {
		return false;
	}
}
