package io.basc.framework.context.repository.annotation;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.context.Context;
import io.basc.framework.context.ContextResolver;
import io.basc.framework.context.ContextResolverExtend;
import io.basc.framework.context.repository.RepositoryBeanDefinition;

public class RepositoryContextResolverExtend implements ContextResolverExtend {
	private final Context context;

	public RepositoryContextResolverExtend(Context context) {
		this.context = context;
	}

	@Override
	public BeanDefinition resolveBeanDefinition(Class<?> sourceClass, ContextResolver chain) {
		Repository repository = sourceClass.getAnnotation(Repository.class);
		if (repository != null) {
			return new RepositoryBeanDefinition(context, sourceClass);
		}
		return ContextResolverExtend.super.resolveBeanDefinition(sourceClass, chain);
	}
}
