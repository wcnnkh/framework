package scw.rabbitmq.method;

import java.io.IOException;

import scw.aop.MethodInvoker;
import scw.io.serialzer.NoTypeSpecifiedSerializer;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.rabbitmq.RabbitmqConsumer;
import scw.rabbitmq.RabbitmqExchange;
import scw.rabbitmq.RabbitmqMessage;

import com.rabbitmq.client.Envelope;

public class RabbitmqMethodExchange {
	private static Logger logger = LoggerUtils
			.getLogger(RabbitmqMethodExchange.class);
	private RabbitmqExchange exchange;
	private NoTypeSpecifiedSerializer serializer;

	public RabbitmqMethodExchange(RabbitmqExchange exchange,
			NoTypeSpecifiedSerializer serializer) {
		this.exchange = exchange;
		this.serializer = serializer;
	}

	public void bindConsumer(String routingKey, String queueName,
			boolean durable, boolean exclusive, boolean autoDelete,
			MethodInvoker invoker) throws IOException {
		logger.info(
				"add Consumer：{}, routingKey={}, queueName={}, durable={}, exclusive={}, autoDelete={}",
				invoker.getMethod(), routingKey, queueName, durable, exclusive,
				autoDelete);
		exchange.bindConsumer(routingKey, queueName, durable, exclusive,
				autoDelete, new RabbitmqMethodConsumer(invoker));
	}

	public void push(String routingKey, Object[] args) throws IOException {
		MethodParametersMessage body = new MethodParametersMessage(args);
		RabbitmqMessage rabbitmqMessage = new RabbitmqMessage(
				serializer.serialize(body));
		exchange.push(routingKey, rabbitmqMessage);
	}

	private final class RabbitmqMethodConsumer implements RabbitmqConsumer {
		private MethodInvoker invoker;

		public RabbitmqMethodConsumer(MethodInvoker invoker) {
			this.invoker = invoker;
		}

		public void handleDelivery(RabbitmqExchange exchange,
				String consumerTag, Envelope envelope, RabbitmqMessage message)
				throws Throwable {
			Class<?>[] parameterTypes = invoker.getMethod().getParameterTypes();
			if (parameterTypes.length == 0) {
				invoker.invoke();
			} else {
				Object body = serializer.deserialize(message.getBody());
				if (body instanceof MethodParametersMessage) {
					invoker.invoke(((MethodParametersMessage) body).getArgs());
				} else {
					invoker.invoke(body);
				}
			}
		}
	}
}
