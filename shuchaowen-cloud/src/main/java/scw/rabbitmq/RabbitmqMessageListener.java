package scw.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import scw.amqp.Message;
import scw.amqp.MessageListener;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class RabbitmqMessageListener extends DefaultConsumer {
	private static Logger logger = LoggerUtils.getLogger(RabbitmqMessageListener.class);
	private final MessageListener messageListener;
	private final boolean multiple;

	public RabbitmqMessageListener(Channel channel, MessageListener messageListener, boolean multiple) {
		super(channel);
		this.messageListener = messageListener;
		this.multiple = multiple;
	}
 
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
		Message message = RabbitmqUitls.toMessage(properties, body);
		try {
			messageListener.onMessage(envelope.getExchange(), envelope.getRoutingKey(), message);
			getChannel().basicAck(envelope.getDeliveryTag(), multiple);
		} catch (IOException e) {
			logger.error(e, "consumerTag={}, envelope={}, properties={}, body={}", consumerTag, envelope, properties, body);
			getChannel().basicReject(envelope.getDeliveryTag(), true);//将消息分配给其他消费者
		}
	}
}
