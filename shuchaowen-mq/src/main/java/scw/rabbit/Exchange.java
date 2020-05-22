package scw.rabbit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import scw.core.Destroy;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class Exchange implements Destroy {
	private static Logger logger = LoggerUtils.getLogger(Exchange.class);
	private String exchangeName;
	private BuiltinExchangeType exchangeType;
	private Channel channel;
	private String dixExchangeName;
	private String delayExchangeName;

	public Exchange(Connection connection, String exchangeName, BuiltinExchangeType exchangeType) throws IOException {
		this.exchangeName = exchangeName;
		this.exchangeType = exchangeType;

		this.channel = connection.createChannel();

		this.dixExchangeName = "dix." + exchangeName;
		channel.exchangeDelete(dixExchangeName);
		channel.exchangeDeclare(dixExchangeName, exchangeType, true);

		this.delayExchangeName = "delay." + exchangeName;
		channel.exchangeDelete(delayExchangeName);
		channel.exchangeDeclare(delayExchangeName, exchangeType, true);

		channel.exchangeDelete(exchangeName);
		channel.exchangeDeclare(exchangeName, exchangeType, true);
	}

	protected boolean isRequeue() {
		return false;
	}

	public void bindConsumer(String routingKey, String queueName, Consumer consumer) throws IOException {
		// 测试使用，删除队列
		channel.queueDelete(queueName);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x-dead-letter-exchange", dixExchangeName);// 死信路由就是自身
		// 声明队列(死信列表和业务队列共用同一队列)
		channel.queueDeclare(queueName, true, false, false, params);

		// 绑定到死信路由
		channel.queueBind(queueName, dixExchangeName, routingKey);

		String delayQueueName = "delay." + queueName;

		// 声明延迟消息队列
		channel.queueDeclare(delayQueueName, true, false, false, params);
		channel.queueBind(delayQueueName, delayExchangeName, routingKey);

		// 声明业务队列
		channel.basicConsume(queueName, false, new ConsumerInternal(channel, consumer));
		channel.exchangeDeclare(exchangeName, exchangeType, true);
		channel.queueBind(queueName, exchangeName, routingKey);
	}

	/**
	 * 发送消息
	 * 
	 * @param routingKey
	 * @param body
	 * @throws IOException
	 */
	public void push(String routingKey, Message message) throws IOException {
		System.out.println(JSONUtils.toJSONString(message));
		if (message.isDelay()) {
			channel.basicPublish(delayExchangeName, routingKey, message.getProperties(), message.getBody());
			return;
		}
		channel.basicPublish(exchangeName, routingKey, message.getProperties(), message.getBody());
	}

	protected boolean isMultiple() {
		return false;
	}

	public void destroy() throws IOException, TimeoutException {
		channel.close();
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
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
				throws IOException {
			Message message = new Message(body, properties);
			try {
				if (message.isDelay()) {
					if (logger.isDebugEnabled()) {
						logger.debug("delay message forward properties: {}", JSONUtils.toJSONString(properties));
					}
					message.setDelay(false);
					push(envelope.getRoutingKey(), message);
					getChannel().basicAck(envelope.getDeliveryTag(), isMultiple());
					return;
				}

				consumer.handleDelivery(Exchange.this, consumerTag, envelope, message);
				getChannel().basicAck(envelope.getDeliveryTag(), isMultiple());
			} catch (Throwable e) {
				logger.error(e, "exchangeName={}, properties={}", exchangeName, JSONUtils.toJSONString(properties));
				message.setDelay(getRetryDelay(), TimeUnit.MILLISECONDS);
				push(envelope.getRoutingKey(), message);
				getChannel().basicAck(envelope.getDeliveryTag(), isMultiple());
			}
		}
	}
}
