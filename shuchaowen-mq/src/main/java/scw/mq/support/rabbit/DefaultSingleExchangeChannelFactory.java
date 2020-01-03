package scw.mq.support.rabbit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import scw.core.Assert;
import scw.core.utils.StringUtils;

public class DefaultSingleExchangeChannelFactory implements SingleExchangeChannelFactory {
	private volatile Map<String, Channel> channelMap = new HashMap<String, Channel>();
	private final Connection connection;
	private final String exchange;
	private final String exchangeType;
	private final boolean autoClose;

	public DefaultSingleExchangeChannelFactory(ConnectionFactory connectionFactory, String exchange,
			String exchangeType) throws IOException, TimeoutException {
		Assert.notNull(connectionFactory);
		Assert.notNull(exchange);
		
		this.connection = connectionFactory.newConnection();
		this.exchange = exchange;
		this.exchangeType = StringUtils.isEmpty(exchangeType) ? BuiltinExchangeType.DIRECT.getType() : exchangeType;
		this.autoClose = true;
	}

	public final String getExchange() {
		return exchange;
	}

	public final String getExchangeType() {
		return exchangeType;
	}

	public Channel getChannel(String name) {
		Channel channel = channelMap.get(name);
		if (channel == null) {
			synchronized (channelMap) {
				channel = channelMap.get(name);
				if (channel == null) {
					try {
						channel = connection.createChannel();
						channel.exchangeDeclare(exchange, exchangeType);
						channelMap.put(name, channel);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return channel;
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

		if (autoClose) {
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
