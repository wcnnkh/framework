package io.basc.framework.beans.factory.component;

import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.core.type.AnnotatedTypeMetadata;
import io.basc.framework.env.EnvironmentCapable;

public interface ComponentCondition {
	boolean matchs(EnvironmentCapable context, BeanDefinitionRegistry registry,
			AnnotatedTypeMetadata annotatedTypeMetadata);
}
