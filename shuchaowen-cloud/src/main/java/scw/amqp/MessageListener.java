package scw.amqp;

import java.io.IOException;

import scw.beans.annotation.Bean;
import scw.lang.Ignore;

@Ignore
@Bean(proxy=false)
public interface MessageListener {
	void onMessage(String exchange, String routingKey, Message message) throws IOException;
}
