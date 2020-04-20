package scw.beans.metadata;

import scw.beans.definition.BeanDefinition;

public class BeanDefinitionAccessor implements BeanDefinitionAware {
	private transient BeanDefinition beanDefinition;

	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
	}

	public void setBeanDefinition(BeanDefinition beanDefinition) {
		this.beanDefinition = beanDefinition;
	}
}
