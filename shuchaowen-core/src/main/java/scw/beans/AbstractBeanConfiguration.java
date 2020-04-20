package scw.beans;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import scw.beans.definition.BeanDefinition;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractBeanConfiguration implements BeanConfiguration {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	
	protected final List<BeanDefinition> beanDefinitions = new LinkedList<BeanDefinition>();

	public Collection<BeanDefinition> getBeans() {
		return beanDefinitions;
	}
}
