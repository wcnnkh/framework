package scw.beans;

import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractBeanFactoryLifeCycle implements BeanFactoryLifeCycle{
	protected final Logger logger = LoggerUtils.getLogger(getClass());
}
