package io.basc.framework.context.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.support.BeanFactoryExecutor;
import io.basc.framework.beans.factory.config.support.DefaultBeanDefinition;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.support.BeanDefinitionRegistryContextPostProcessor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.ExecutionInterceptors;
import io.basc.framework.execution.reflect.ConstructorExecutor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

class AnnotationBeanDefinitionRegistryContextPostProcessor extends BeanDefinitionRegistryContextPostProcessor {

	@Override
	protected boolean canResolveBeanDefinition(Class<?> clazz) {
		return AnnotatedElementUtils.hasAnnotation(clazz, Component.class);
	}

	@Override
	protected boolean canResolveBeanDefinition(Class<?> clazz, BeanDefinition originBeanDefinition, Method method) {
		return AnnotatedElementUtils.hasAnnotation(method, Bean.class);
	}

	private ExecutionInterceptor getExecutionInterceptor(ConfigurableContext context, EnableAop enableAop) {
		Elements<ExecutionInterceptor> nameExecutionInterceptors = Elements.forArray(enableAop.interceptorNames())
				.map((e) -> context.getBean(e, ExecutionInterceptor.class));
		Elements<ExecutionInterceptor> classExecutionInterceptors = Elements.forArray(enableAop.interceptors())
				.map((e) -> context.getBean(e));
		Elements<ExecutionInterceptor> executionInterceptors = nameExecutionInterceptors
				.concat(classExecutionInterceptors);
		return new ExecutionInterceptors(executionInterceptors);
	}

	@Override
	protected DefaultBeanDefinition<ConstructorExecutor> resolveBeanDefinition(ConfigurableContext context,
			Class<?> clazz) {
		DefaultBeanDefinition<ConstructorExecutor> beanDefinition = super.resolveBeanDefinition(context, clazz);
		beanDefinition.setSingleton(isSingleton(clazz));
		beanDefinition.setScope(getScope(clazz));
		EnableAop enableAop = AnnotatedElementUtils.getMergedAnnotation(clazz, EnableAop.class);
		if (enableAop != null) {
			Elements<? extends ConstructorExecutor> executors = beanDefinition.getExecutors();
			executors = executors.peek((constructor) -> {
				constructor.setAop(context.getAop());
				constructor.setAopInterfaces(enableAop.interfaces());
				constructor.setExecutionInterceptor(getExecutionInterceptor(context, enableAop));
			});
			beanDefinition.setExecutors(executors);
		}
		return beanDefinition;
	}

	@Override
	protected DefaultBeanDefinition<BeanFactoryExecutor> resolveBeanDefinition(ConfigurableContext context,
			Class<?> clazz, String originBeanName, BeanDefinition originBeanDefinition, Method method) {
		DefaultBeanDefinition<BeanFactoryExecutor> beanDefinition = super.resolveBeanDefinition(context, clazz,
				originBeanName, originBeanDefinition, method);
		beanDefinition.setSingleton(isSingleton(method));
		beanDefinition.setScope(getScope(method));
		EnableAop enableAop = AnnotatedElementUtils.getMergedAnnotation(clazz, EnableAop.class);
		if (enableAop != null) {
			Elements<? extends BeanFactoryExecutor> executors = beanDefinition.getExecutors();
			executors = executors.peek((constructor) -> {
				constructor.setAop(context.getAop());
				constructor.setAopInterfaces(enableAop.interfaces());
				constructor.setExecutionInterceptor(getExecutionInterceptor(context, enableAop));
			});
			beanDefinition.setExecutors(executors);
		}
		return beanDefinition;
	}

	protected io.basc.framework.beans.factory.Scope getScope(AnnotatedElement annotatedElement) {
		Scope scope = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Scope.class);
		if (scope != null) {
			return io.basc.framework.beans.factory.Scope.getFirstOrCreate(scope.value(),
					io.basc.framework.beans.factory.Scope.class,
					() -> new io.basc.framework.beans.factory.Scope(scope.value()));
		}
		return io.basc.framework.beans.factory.Scope.DEFAULT;
	}

	protected boolean isSingleton(AnnotatedElement annotatedElement) {
		Singleton singleton = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Singleton.class);
		if (singleton != null) {
			return singleton.value();
		}
		return true;
	}

	@Override
	protected String getBeanName(Class<?> clazz) {
		Component component = AnnotatedElementUtils.getMergedAnnotation(clazz, Component.class);
		if (component != null && StringUtils.isNotEmpty(component.value())) {
			return component.value();
		}
		return super.getBeanName(clazz);
	}

	@Override
	protected Elements<String> getAliasNames(Class<?> clazz, Method method) {
		Bean bean = AnnotatedElementUtils.getMergedAnnotation(method, Bean.class);
		return Elements.forArray(bean.value()).concat(Elements.forArray(bean.name()));
	}
}
