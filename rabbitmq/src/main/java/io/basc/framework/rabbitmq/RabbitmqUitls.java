package io.basc.framework.rabbitmq;

import io.basc.framework.amqp.Message;
import io.basc.framework.amqp.MessageProperties;

import com.rabbitmq.client.AMQP.BasicProperties;

public class RabbitmqUitls {
	public static BasicProperties toBasicProperties(MessageProperties messageProperties) {
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

	public static Message toMessage(com.rabbitmq.client.AMQP.BasicProperties basicProperties, byte[] body) {
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
}
