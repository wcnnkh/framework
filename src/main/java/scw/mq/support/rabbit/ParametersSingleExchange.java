package scw.mq.support.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;

import scw.core.DefaultParameters;
import scw.core.Destroy;
import scw.core.Parameters;
import scw.core.serializer.NoTypeSpecifiedSerializer;
import scw.core.serializer.support.JavaSerializer;
import scw.mq.amqp.ParametersExchange;

public class ParametersSingleExchange extends SingleExchange<Parameters> implements ParametersExchange, Destroy {
	private boolean destory;

	public ParametersSingleExchange(SingleExchangeChannelFactory channelFactory, NoTypeSpecifiedSerializer serializer)
			throws IOException, TimeoutException {
		super(channelFactory, serializer);
		this.destory = false;
	}

	public ParametersSingleExchange(ConnectionFactory connectionFactory, String exchange, String exchangeType)
			throws IOException, TimeoutException {
		this(new DefaultSingleExchangeChannelFactory(connectionFactory, exchange, exchangeType),
				JavaSerializer.SERIALIZER);
		this.destory = true;
	}

	public ParametersSingleExchange(ConnectionFactory connectionFactory, String exchange)
			throws IOException, TimeoutException {
		this(new DefaultSingleExchangeChannelFactory(connectionFactory, exchange, BuiltinExchangeType.DIRECT.getType()),
				JavaSerializer.SERIALIZER);
		this.destory = true;
	}

	public void pushArgs(String routingKey, Object... args) {
		push(routingKey, new DefaultParameters(args));
	}

	public void destroy() {
		if (destory) {
			getChannelFactory().destroy();
		}
	}
}
