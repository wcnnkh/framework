package scw.mq.rabbit;

import com.rabbitmq.client.Channel;

public interface ChannelFactory {
	Channel getChannel(String name);
}
