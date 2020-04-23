package scw.beans;


public class BeanDefinitionAccessor implements BeanDefinitionAware {
	private transient BeanDefinition beanDefinition;

	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
	}

	public void setBeanDefinition(BeanDefinition beanDefinition) {
		this.beanDefinition = beanDefinition;
	}
}
