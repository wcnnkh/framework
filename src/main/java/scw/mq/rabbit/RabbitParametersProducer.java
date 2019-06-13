package scw.mq.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.ConnectionFactory;

import scw.core.DefaultParameters;
import scw.core.Parameters;
import scw.mq.ParametersProducer;

public class RabbitParametersProducer extends RabbitProducer<Parameters> implements ParametersProducer {
	public RabbitParametersProducer(ConnectionFactory connectionFactory, String exchange, String exchangeType)
			throws IOException, TimeoutException {
		super(connectionFactory, exchange, exchangeType);
	}

	public RabbitParametersProducer(ConnectionFactory connectionFactory, String exchange, String exchangeType,
			boolean mandatory, boolean immediate) throws IOException, TimeoutException {
		super(connectionFactory, exchange, exchangeType, mandatory, immediate);
	}

	public void pushParameters(String name, Object... parameters) {
		push(name, new DefaultParameters(parameters));
	}

}
