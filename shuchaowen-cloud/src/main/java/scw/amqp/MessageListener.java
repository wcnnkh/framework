package scw.amqp;

public interface MessageListener {
	void onMessage(String exchange, String routingKey, Message message) throws Throwable;
}
