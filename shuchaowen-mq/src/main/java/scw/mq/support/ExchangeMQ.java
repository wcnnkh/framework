package scw.mq.support;

import scw.core.Consumer;
import scw.mq.MQ;
import scw.mq.amqp.Exchange;

public class ExchangeMQ<T> implements MQ<T> {
	private final Exchange<T> exchange;

	public ExchangeMQ(Exchange<T> exchange) {
		this.exchange = exchange;
	}

	public void push(String name, T message) {
		exchange.push(name, message);
	}

	public void bindConsumer(String name, Consumer<T> consumer) {
		exchange.bindConsumer(name, name, consumer);
	}
}