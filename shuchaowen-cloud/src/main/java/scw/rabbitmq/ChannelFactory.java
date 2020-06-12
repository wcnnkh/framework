package scw.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;

public interface ChannelFactory {
	Channel getChannel() throws IOException;
}
