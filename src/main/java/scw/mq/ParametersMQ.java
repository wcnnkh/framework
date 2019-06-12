package scw.mq;

import scw.core.Parameters;

public interface ParametersMQ extends MQ<Parameters> {
	void pushParameters(String name, Object... parameters);
}
