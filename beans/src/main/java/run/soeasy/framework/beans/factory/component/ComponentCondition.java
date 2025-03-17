package run.soeasy.framework.beans.factory.component;

import run.soeasy.framework.beans.factory.config.BeanDefinitionRegistry;
import run.soeasy.framework.core.env.EnvironmentCapable;
import run.soeasy.framework.core.type.AnnotatedTypeMetadata;

public interface ComponentCondition {
	boolean matchs(EnvironmentCapable context, BeanDefinitionRegistry registry,
			AnnotatedTypeMetadata annotatedTypeMetadata);
}
