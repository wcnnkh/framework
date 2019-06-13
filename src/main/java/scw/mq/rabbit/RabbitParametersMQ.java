package scw.mq.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.ConnectionFactory;

import scw.core.DefaultParameters;
import scw.core.Parameters;
import scw.mq.ParametersMQ;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public class RabbitParametersMQ extends RabbitMQ<Parameters> implements ParametersMQ {

	public RabbitParametersMQ(ConnectionFactory connectionFactory, String exchange, String routingKey, boolean durable,
			boolean exclusive, boolean autoDelete) throws IOException, TimeoutException {
		super(connectionFactory, exchange, routingKey, durable, exclusive, autoDelete);
	}

	public RabbitParametersMQ(ConnectionFactory connectionFactory, String exchange, String exchangeType,
			String routingKey, boolean durable, boolean exclusive, boolean autoDelete)
			throws IOException, TimeoutException {
		super(connectionFactory, exchange, exchangeType, routingKey, durable, exclusive, autoDelete);
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
}
