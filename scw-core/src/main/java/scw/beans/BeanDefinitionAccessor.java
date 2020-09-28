package scw.beans;

public interface BeanDefinitionAccessor {
	static final Class<?>[] PROXY_INTERFACES = new Class<?>[] { BeanDefinitionAccessor.class };
	static final String METHOD_NAME = "getBeanDefinition";

	BeanDefinition getBeanDefinition();
}