package io.basc.framework.mvc.annotation;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.annotation.Annotations;
import io.basc.framework.mvc.ActionResolver;
import io.basc.framework.mvc.ActionResolverExtend;
import io.basc.framework.mvc.action.ActionInterceptor;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;

@Provider
public class AnnotationActionResolverExtend implements ActionResolverExtend {
	@Override
	public String getControllerId(Class<?> sourceClass, Method method, ActionResolver chain) {
		Controller controller = Annotations.getAnnotation(Controller.class, sourceClass, method);
		if (controller != null && StringUtils.isNotEmpty(controller.value())) {
			return controller.value();
		}
		return ActionResolverExtend.super.getControllerId(sourceClass, method, chain);
	}

	@Override
	public Collection<String> getActionInterceptorNames(Class<?> sourceClass, Method method, ActionResolver chain) {
		LinkedHashSet<String> sets = new LinkedHashSet<String>();
		ActionInterceptors actionInterceptors = sourceClass.getAnnotation(ActionInterceptors.class);
		if (actionInterceptors != null) {
			for (String name : actionInterceptors.name()) {
				sets.add(name);
			}
			for (Class<? extends ActionInterceptor> f : actionInterceptors.value()) {
				sets.add(f.getName());
			}
		}

		actionInterceptors = method.getAnnotation(ActionInterceptors.class);
		if (actionInterceptors != null) {
			sets.clear();
			for (String name : actionInterceptors.name()) {
				sets.add(name);
			}
			for (Class<? extends ActionInterceptor> f : actionInterceptors.value()) {
				sets.add(f.getName());
			}
		}
		Collection<String> names = ActionResolverExtend.super.getActionInterceptorNames(sourceClass, method, chain);
		if (!CollectionUtils.isEmpty(names)) {
			sets.addAll(names);
		}
		return sets;
	}
}
