package io.basc.framework.context.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.basc.framework.context.Context;
import io.basc.framework.context.ContextResolver;
import io.basc.framework.context.ContextResolverExtend;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.util.CollectionUtils;

public class RepositoryContextResolverExtend implements ContextResolverExtend {
	private final Context context;

	public RepositoryContextResolverExtend(Context context) {
		this.context = context;
	}

	@Override
	public Collection<BeanDefinition> resolveBeanDefinitions(Class<?> clazz, ContextResolver chain) {
		if (context.containsDefinition(clazz.getName())) {
			return ContextResolverExtend.super.resolveBeanDefinitions(clazz, chain);
		}
		List<BeanDefinition> list = null;
		Repository repository = clazz.getAnnotation(Repository.class);
		if (repository != null) {
			if (list == null) {
				list = new ArrayList<BeanDefinition>(8);
			}

			RepositoryBeanDefinition definition = new RepositoryBeanDefinition(context, clazz);
			list.add(definition);
		}

		Collection<BeanDefinition> superDefinitions = ContextResolverExtend.super.resolveBeanDefinitions(clazz, chain);
		if (!CollectionUtils.isEmpty(superDefinitions)) {
			if (list == null) {
				list = new ArrayList<BeanDefinition>(8);
			}
			list.addAll(superDefinitions);
		}
		return list == null ? Collections.emptyList() : list;
	}
}
