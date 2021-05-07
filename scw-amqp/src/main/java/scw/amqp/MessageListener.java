package scw.amqp;

import java.io.IOException;

@FunctionalInterface
public interface MessageListener {
	void onMessage(String exchange, String routingKey, Message message) throws IOException;
}
