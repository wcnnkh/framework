package scw.logger;

import scw.core.Destroy;

public interface ILoggerFactory extends Destroy{

	Logger getLogger(String name);
}
