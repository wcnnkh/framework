package scw.rabbitmq;

import com.rabbitmq.client.Channel;

public interface ChannelFactory {
	Channel getChannel();
}
