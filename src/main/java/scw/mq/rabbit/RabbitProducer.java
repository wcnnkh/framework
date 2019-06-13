package scw.mq.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitProducer<T> extends AbstractRabbitProducer<T> {
	private final boolean mandatory;
	private final boolean immediate;

	public RabbitProducer(ConnectionFactory connectionFactory, String exchange, String exchangeType)
			throws IOException, TimeoutException {
		this(connectionFactory, exchange, exchangeType, false, false);
	}

	public RabbitProducer(ConnectionFactory connectionFactory, String exchange, String exchangeType,
			boolean mandatory, boolean immediate) throws IOException, TimeoutException {
		super(connectionFactory, exchange, exchangeType);
		this.mandatory = mandatory;
		this.immediate = immediate;
	}

	@Override
	protected boolean isMandatory(String name) {
		return mandatory;
	}

	@Override
	protected boolean isImmediate(String name) {
		return immediate;
	}

	@Override
	protected BasicProperties getBasicProperties(String name) {
		return null;
	}

}
