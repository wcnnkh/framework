package scw.mq.rabbit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

public class RabbitUtils {
	private RabbitUtils() {
	}

	public static void basicPublish(Channel channel, String exchange, String routingKey, BasicProperties props,
			byte[] data) {
		try {
			channel.basicPublish(exchange, routingKey, props, data);
		} catch (IOException e) {
			throw new RabbitException(e);
		}
	}

	public static void basicPublish(Channel channel, String exchange, String routingKey, byte[] data) {
		basicPublish(channel, exchange, routingKey, null, data);
	}

	public static void basicPublish(Channel channel, String exchange, String routingKey, String data,
			String charsetName) {
		try {
			basicPublish(channel, exchange, routingKey, data.getBytes(charsetName));
		} catch (UnsupportedEncodingException e) {
			throw new RabbitException(e);
		}
	}
}
