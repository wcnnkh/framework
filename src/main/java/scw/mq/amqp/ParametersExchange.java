package scw.mq.amqp;

import scw.core.Parameters;
import scw.mq.ParameterProducer;

public interface ParametersExchange extends Exchange<Parameters>, ParameterProducer{
	void pushArgs(String routingKey, Object ...args);
}
