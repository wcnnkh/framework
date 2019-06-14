package scw.mq.support.rabbit;

import com.rabbitmq.client.Channel;

import scw.core.Destroy;

public interface ChannelFactory extends Destroy{
	Channel getChannel(String name);
}
