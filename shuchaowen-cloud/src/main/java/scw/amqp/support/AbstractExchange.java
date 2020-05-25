package scw.amqp.support;

import java.io.IOException;

import scw.amqp.Message;
import scw.amqp.MessageListener;
import scw.amqp.MessageProperties;
import scw.amqp.QueueDeclare;
import scw.aop.MethodInvoker;
import scw.io.serialzer.NoTypeSpecifiedSerializer;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractExchange implements Exchange {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	private NoTypeSpecifiedSerializer serializer;

	public AbstractExchange(NoTypeSpecifiedSerializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public void bind(String routingKey, QueueDeclare queueDeclare, MethodInvoker methodInvoker) {
		logger.info("add message listener：{}, routingKey={}, queueDeclare={}", methodInvoker.getMethod(), routingKey,
				queueDeclare);
		bind(routingKey, queueDeclare, new MethodMessageListener(methodInvoker));
	}

	@Override
	public void push(String routingKey, Message message) {
		push(routingKey, message, message.getBody());
	}

	protected abstract void push(String routingKey, MessageProperties messageProperties, byte[] body);

	@Override
	public void push(String routingKey, MethodMessage methodMessage) {
		try {
			push(routingKey, methodMessage, serializer.serialize(methodMessage.getArgs()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private final class MethodMessageListener implements MessageListener {
		private MethodInvoker invoker;

		public MethodMessageListener(MethodInvoker invoker) {
			this.invoker = invoker;
		}

		@Override
		public void onMessage(String exchange, String routingKey, Message message) throws Throwable {
			Object[] args = serializer.deserialize(message.getBody());
			invoker.invoke(args);
		}
	}
}
