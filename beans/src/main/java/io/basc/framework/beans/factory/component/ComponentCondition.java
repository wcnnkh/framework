package io.basc.framework.beans.factory.component;

import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.core.env.EnvironmentCapable;
import io.basc.framework.core.type.AnnotatedTypeMetadata;

public interface ComponentCondition {
	boolean matchs(EnvironmentCapable context, BeanDefinitionRegistry registry,
			AnnotatedTypeMetadata annotatedTypeMetadata);
}
