package scw.mq;

import scw.core.Parameters;

public interface ParametersProducer extends Producer<Parameters> {
	void pushParameters(String name, Object... parameters);
}
