package scw.rabbitmq;

import java.io.IOException;

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
import scw.io.serialzer.SerializerUtils;
import scw.json.JSONUtils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class RabbitmqExchange extends LocalTransactionExchange {
	private static final String DIX_PREFIX = "scw.dix.";
	private static final String DELAY_PREFIX = "scw.delay.";
	private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";

	private ThreadLocal<Channel> channelThreadLocal;
	private final ExchangeDeclare exchangeDeclare;
	private final ExchangeDeclare dixExchangeDeclare;
	private final ExchangeDeclare delayExchangeDeclare;
	private final QueueDeclare dixQueueDeclare;
	private final QueueDeclare delayQueueDeclare;

	private void checkName(String name) {
		Assert.requiredArgument(
				StringUtils.isNotEmpty(name) && !name.startsWith(DIX_PREFIX) && !name.startsWith(DELAY_PREFIX), name);
	}

	public RabbitmqExchange(NoTypeSpecifiedSerializer serializer, final Connection connection,
			ExchangeDeclare exchangeDeclare, CompleteService completeService, String beanId) throws IOException {
		super(serializer, completeService, beanId);
		checkName(exchangeDeclare.getName());
		//死信路由
		this.dixExchangeDeclare = SerializerUtils.clone(exchangeDeclare)
				.setName(DIX_PREFIX + exchangeDeclare.getName());
		
		//死信队列
		this.dixQueueDeclare = new QueueDeclare(dixExchangeDeclare.getName() + ".queue");
		
		//延迟消息路由
		this.delayExchangeDeclare = SerializerUtils.clone(exchangeDeclare)
				.setName(DELAY_PREFIX + exchangeDeclare.getName());
		
		//延迟消息队列
		this.delayQueueDeclare = new QueueDeclare(delayExchangeDeclare.getName() + ".queue");
		//设置延迟消息的死信队列(注意: 不要给此队列绑定消费者，这样在消息过期后会进入死信队列，在死信队列中将消息发给指定队列那么就实现了消息的延迟发送)
		delayQueueDeclare.setArgument(X_DEAD_LETTER_EXCHANGE, dixExchangeDeclare.getName());

		
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
		
		//声明死信路由和队列
		declare(dixExchangeDeclare, dixQueueDeclare);
		
		//声明延迟消息路由和队列
		declare(exchangeDeclare, delayQueueDeclare);
	}
	
	protected final Channel getChannel(){
		return channelThreadLocal.get();
	}

	private void declare(ExchangeDeclare exchangeDeclare, QueueDeclare queueDeclare)
			throws IOException {
		if(exchangeDeclare != null){
			getChannel().exchangeDeclare(exchangeDeclare.getName(), exchangeDeclare.getType(), exchangeDeclare.isDurable(),
					exchangeDeclare.isAutoDelete(), exchangeDeclare.isInternal(), exchangeDeclare.getArguments());
		}
		if(queueDeclare != null){
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
