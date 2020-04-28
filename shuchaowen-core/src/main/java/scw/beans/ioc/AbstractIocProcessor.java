package scw.beans.ioc;

import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractIocProcessor implements IocProcessor {
	protected Logger logger = LoggerUtils.getLogger(getClass());
}
