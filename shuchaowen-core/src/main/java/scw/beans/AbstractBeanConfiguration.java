package scw.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractBeanConfiguration implements BeanConfiguration {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	
	protected final List<BeanDefinition> beanDefinitions = new LinkedList<BeanDefinition>();
	protected final Map<String, String> nameMappingMap = new HashMap<String, String>();

	public Collection<BeanDefinition> getBeans() {
		return beanDefinitions;
	}
	
	public Map<String, String> getNameMappingMap() {
		return nameMappingMap;
	}
}
