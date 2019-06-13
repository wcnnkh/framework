package scw.mq.rabbit;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;

import scw.core.Destroy;

/**
 * 一个简单的mq处理
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class RabbitMQ<T> extends AbstractRabbitMQ<T> implements Destroy {
	private final String routingKey;
	private final boolean durable;
	private final boolean exclusive;
	private final boolean autoDelete;

	public RabbitMQ(ConnectionFactory connectionFactory, String exchange, String routingKey, boolean durable,
			boolean exclusive, boolean autoDelete) throws IOException, TimeoutException {
		this(connectionFactory, exchange, BuiltinExchangeType.DIRECT.name(), routingKey, durable, exclusive,
				autoDelete);
	}

	public RabbitMQ(ConnectionFactory connectionFactory, String exchange, String exchangeType, String routingKey,
			boolean durable, boolean exclusive, boolean autoDelete) throws IOException, TimeoutException {
		super(connectionFactory, exchange, exchangeType);
		this.routingKey = routingKey;
		this.durable = durable;
		this.exclusive = exclusive;
		this.autoDelete = autoDelete;
	}

	@Override
	protected String getRoutingKey(String name) {
		return routingKey;
	}

	@Override
	protected com.rabbitmq.client.AMQP.BasicProperties getBasicProperties(String name) {
		return null;
	}

	@Override
	protected boolean isMandatory(String name) {
		return false;
	}

	@Override
	protected boolean isImmediate(String name) {
		return false;
	}

	@Override
	protected boolean isDurable(String name) {
		return durable;
	}

	@Override
	protected boolean isExclusive(String name) {
		return exclusive;
	}

	@Override
	protected boolean isAutoDelete(String name) {
		return autoDelete;
	}

	@Override
	protected Map<String, Object> getArguments(String name) {
		return null;
	}
}
