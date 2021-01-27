package scw.amqp;

import java.io.IOException;

import scw.lang.Ignore;

@Ignore
public interface MessageListener {
	void onMessage(String exchange, String routingKey, Message message) throws IOException;
}
