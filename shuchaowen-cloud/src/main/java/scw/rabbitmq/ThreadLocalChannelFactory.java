package scw.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE)
public class ThreadLocalChannelFactory implements ChannelFactory {
	private ThreadLocal<Channel> channelThreadLocal = new ThreadLocal<Channel>();
	private final Connection connection;

	public ThreadLocalChannelFactory(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Channel getChannel() throws IOException {
		Channel channel = channelThreadLocal.get();
		if (channel != null && channel.isOpen()) {
			return channel;
		}

		channel = connection.createChannel();
		channelThreadLocal.set(channel);
		return channel;
	}

}
