package scw.mq.rabbit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import scw.core.Destroy;
import scw.core.serializer.NoTypeSpecifiedSerializer;
import scw.core.serializer.support.JavaSerializer;

/**
 * 一个简单的mq处理
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class SimpleRabbitMQ<T> extends AbstractRabbitMQ<T> implements Destroy {
	private volatile Map<String, Channel> channelMap = new HashMap<String, Channel>();
	private final Connection connection;
	private final String exchange;
	private final String exchangeType;
	private final String routingKey;
	private final boolean durable;
	private final boolean exclusive;
	private final boolean autoDelete;

	public SimpleRabbitMQ(ConnectionFactory connectionFactory, String exchange, String routingKey, boolean durable,
			boolean exclusive, boolean autoDelete) throws IOException, TimeoutException {
		this(connectionFactory, exchange, BuiltinExchangeType.DIRECT.name(), routingKey, durable, exclusive,
				autoDelete);
	}

	public SimpleRabbitMQ(ConnectionFactory connectionFactory, String exchange, String exchangeType, String routingKey,
			boolean durable, boolean exclusive, boolean autoDelete) throws IOException, TimeoutException {
		this(connectionFactory, exchange, exchangeType, routingKey, JavaSerializer.SERIALIZER, durable, exclusive,
				autoDelete);
	}

	public SimpleRabbitMQ(ConnectionFactory connectionFactory, String exchange, String exchangeType, String routingKey,
			NoTypeSpecifiedSerializer serializer, boolean durable, boolean exclusive, boolean autoDelete)
			throws IOException, TimeoutException {
		super(serializer);
		this.connection = connectionFactory.newConnection();
		this.exchange = exchange;
		this.exchangeType = exchangeType;
		this.routingKey = routingKey;
		this.durable = durable;
		this.exclusive = exclusive;
		this.autoDelete = autoDelete;
	}

	@Override
	protected String getExchange(String name) {
		return exchange;
	}

	@Override
	protected Channel getChannel(String name) {
		Channel channel = channelMap.get(name);
		if (channel == null) {
			synchronized (channelMap) {
				channel = channelMap.get(name);
				if (channel == null) {
					try {
						channel = connection.createChannel();
						channel.exchangeDeclare(getExchange(name), exchangeType);
						channelMap.put(name, channel);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return channel;
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

	public void destroy() {
		for (Entry<String, Channel> entry : channelMap.entrySet()) {
			try {
				entry.getValue().close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		}

		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
