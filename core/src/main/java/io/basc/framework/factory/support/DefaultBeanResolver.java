package io.basc.framework.factory.support;

import io.basc.framework.factory.ConfigurableBeanResolver;
import io.basc.framework.factory.annotation.AnnotationFactoryInstanceResolverExtend;

public class DefaultBeanResolver extends ConfigurableBeanResolver {
	public DefaultBeanResolver() {
		addService(new AnnotationFactoryInstanceResolverExtend());
	}
}
