package scw.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import scw.core.instance.annotation.Configuration;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

@Configuration(order=Integer.MIN_VALUE)
public class ThreadLocalChannelFactory implements ChannelFactory{
	private static Logger logger = LoggerUtils.getLogger(ThreadLocalChannelFactory.class);
	private ThreadLocal<Channel> channelThreadLocal;
	private final Connection connection;
	
	
	public ThreadLocalChannelFactory(Connection connection){
		this.connection = connection;
		channelThreadLocal = new ThreadLocal<Channel>() {
			@Override
			protected Channel initialValue() {
				try {
					return ThreadLocalChannelFactory.this.connection.createChannel();
				} catch (IOException e) {
					logger.error(e, "create channel error");
					return null;
				}
			}
		};
	}

	@Override
	public Channel getChannel() {
		return channelThreadLocal.get();
	}
	
}
