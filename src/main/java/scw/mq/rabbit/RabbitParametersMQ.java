package scw.mq.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;

import scw.beans.annotation.AsyncComplete;
import scw.core.DefaultParameters;
import scw.core.Parameters;
import scw.core.serializer.NoTypeSpecifiedSerializer;
import scw.core.serializer.support.JavaSerializer;
import scw.mq.ParametersMQ;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public class RabbitParametersMQ extends SimpleRabbitMQ<Parameters> implements ParametersMQ {
	
	public RabbitParametersMQ(ConnectionFactory connectionFactory, String exchange, String routingKey, boolean durable,
			boolean exclusive, boolean autoDelete) throws IOException, TimeoutException {
		super(connectionFactory, exchange, BuiltinExchangeType.DIRECT.name(), routingKey, durable, exclusive,
				autoDelete);
	}

	public RabbitParametersMQ(ConnectionFactory connectionFactory, String exchange, String exchangeType,
			String routingKey, boolean durable, boolean exclusive, boolean autoDelete)
			throws IOException, TimeoutException {
		super(connectionFactory, exchange, exchangeType, routingKey, JavaSerializer.SERIALIZER, durable, exclusive,
				autoDelete);
	}

	public RabbitParametersMQ(ConnectionFactory connectionFactory, String exchange, String exchangeType,
			String routingKey, NoTypeSpecifiedSerializer serializer, boolean durable, boolean exclusive,
			boolean autoDelete) throws IOException, TimeoutException {
		super(connectionFactory, exchange, exchangeType, routingKey, serializer, durable, exclusive, autoDelete);
	}

	public void pushParameters(final String name, Object... parameters) {
		final Parameters ps = new DefaultParameters(parameters);
		if (TransactionManager.hasTransaction()) {
			TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
				@Override
				public void complete() {
					push(name, ps);
				}
			});
		} else {
			push(name, ps);
		}
	}

	@AsyncComplete
	@Override
	public void push(String name, Parameters message) {
		super.push(name, message);
	}
}
