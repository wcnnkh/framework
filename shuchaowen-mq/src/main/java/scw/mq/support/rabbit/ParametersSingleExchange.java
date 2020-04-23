package scw.mq.support.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;

import scw.core.SimpleParameters;
import scw.core.Destroy;
import scw.core.Parameters;
import scw.io.JavaSerializer;
import scw.io.NoTypeSpecifiedSerializer;
import scw.mq.amqp.ParametersExchange;

public class ParametersSingleExchange extends SingleExchange<Parameters> implements ParametersExchange, Destroy {
	private boolean destory;

	public ParametersSingleExchange(SingleExchangeChannelFactory channelFactory, NoTypeSpecifiedSerializer serializer,
			boolean errorAutoAppend, boolean asyncComplete, boolean alwaysNotify) throws IOException, TimeoutException {
		super(channelFactory, serializer, errorAutoAppend, asyncComplete, alwaysNotify);
		this.destory = false;
	}

	public ParametersSingleExchange(ConnectionFactory connectionFactory, String exchange, String exchangeType,
			boolean errorAutoAppend, boolean asyncComplete, boolean alwaysNotify) throws IOException, TimeoutException {
		this(new DefaultSingleExchangeChannelFactory(connectionFactory, exchange, exchangeType),
				JavaSerializer.SERIALIZER, errorAutoAppend, asyncComplete, alwaysNotify);
		this.destory = true;
	}

	public ParametersSingleExchange(ConnectionFactory connectionFactory, String exchange, boolean errorAutoAppend,
			boolean asyncComplete, boolean alwaysNotify) throws IOException, TimeoutException {
		this(new DefaultSingleExchangeChannelFactory(connectionFactory, exchange, BuiltinExchangeType.DIRECT.getType()),
				JavaSerializer.SERIALIZER, errorAutoAppend, asyncComplete, alwaysNotify);
		this.destory = true;
	}

	public ParametersSingleExchange(ConnectionFactory connectionFactory, String exchange)
			throws IOException, TimeoutException {
		this(connectionFactory, exchange, true, true, true);
	}

	public void pushArgs(String routingKey, Object... args) {
		push(routingKey, new SimpleParameters(args));
	}

	public void destroy() throws Exception {
		if (destory) {
			getChannelFactory().destroy();
		}
	}
}
