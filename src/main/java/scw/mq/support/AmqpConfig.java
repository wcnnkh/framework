package scw.mq.support;

import scw.mq.amqp.AmqpQueueConfig;
import scw.mq.amqp.Exchange;

@SuppressWarnings("rawtypes")
public class AmqpConfig implements AmqpQueueConfig {
	private String routingKey;
	private String queueName;
	private boolean durable = true;
	private boolean exclusive;
	private boolean autoDelete;
	private Exchange exchange;

	public Exchange getExchange() {
		return exchange;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public boolean isDurable() {
		return durable;
	}

	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	public boolean isExclusive() {
		return exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	public boolean isAutoDelete() {
		return autoDelete;
	}

	public void setAutoDelete(boolean autoDelete) {
		this.autoDelete = autoDelete;
	}
}
