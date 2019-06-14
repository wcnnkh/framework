package scw.mq.amqp;

import scw.core.Parameters;

public interface ParametersExchange extends Exchange<Parameters> {
	void pushArgs(String routingKey, Object ...args);
}
