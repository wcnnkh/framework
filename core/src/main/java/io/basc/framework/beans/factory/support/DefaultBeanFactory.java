package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.factory.NoSuchBeanDefinitionException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.execution.param.ParameterExtractors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DefaultBeanFactory extends AbstractHierarchicalBeanFactory {
	private final Scope scope;
	private final ParameterExtractors parameterExtractors = new BeanFactoryParameterExtractor(this);

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		BeanDefinition beanDefinition = getBeanDefinitionOfCache(beanName);
		if (beanDefinition == null) {
			throw new NoSuchBeanDefinitionException(beanName);
		}
		return beanDefinition;
	}
}
