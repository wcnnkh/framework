package io.basc.framework.beans.factory.annotation;

import io.basc.framework.beans.factory.component.ComponentCondition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.core.type.AnnotatedTypeMetadata;
import io.basc.framework.env.EnvironmentCapable;

class OnBeanCondition implements ComponentCondition {

	@Override
	public boolean matchs(EnvironmentCapable context, BeanDefinitionRegistry registry,
			AnnotatedTypeMetadata annotatedTypeMetadata) {
		return true;
	}

}
