package io.basc.framework.context.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.support.BeanFactoryExecutor;
import io.basc.framework.beans.factory.config.support.DefaultBeanDefinition;
import io.basc.framework.context.config.Condition;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.context.config.support.BeanDefinitionRegistryContextPostProcessor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.ExecutionInterceptors;
import io.basc.framework.execution.reflect.ReflectionConstructor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;

class AnnotationBeanDefinitionRegistryContextPostProcessor extends BeanDefinitionRegistryContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableApplicationContext context) throws Throwable {
		if (!context.getBeanProvider(OnBeanCondition.class).isEmpty()) {
			// 不存在那么注册一个默认的
			OnBeanCondition onBeanCondition = new OnBeanCondition();
			context.registerSingleton(OnBeanCondition.class.getName(), onBeanCondition);
		}
		super.postProcessContext(context);
	}

	@Override
	protected boolean canResolveBeanDefinition(Class<?> clazz) {
		return AnnotatedElementUtils.hasAnnotation(clazz, Component.class);
	}

	@Override
	protected boolean canResolveBeanDefinition(Class<?> clazz, BeanDefinition originBeanDefinition, Method method) {
		return AnnotatedElementUtils.hasAnnotation(method, Bean.class);
	}

	private ExecutionInterceptor getExecutionInterceptor(ConfigurableApplicationContext context, Aop enableAop) {
		Elements<ExecutionInterceptor> nameExecutionInterceptors = Elements.forArray(enableAop.interceptorNames())
				.map((e) -> context.getBean(e, ExecutionInterceptor.class));
		Elements<ExecutionInterceptor> classExecutionInterceptors = Elements.forArray(enableAop.interceptors())
				.map((e) -> context.getBean(e));
		Elements<ExecutionInterceptor> executionInterceptors = nameExecutionInterceptors
				.concat(classExecutionInterceptors);
		return new ExecutionInterceptors(executionInterceptors);
	}

	@Override
	protected DefaultBeanDefinition<ReflectionConstructor> resolveBeanDefinition(ConfigurableApplicationContext context,
			Class<?> clazz) {
		DefaultBeanDefinition<ReflectionConstructor> beanDefinition = super.resolveBeanDefinition(context, clazz);
		beanDefinition.setSingleton(isSingleton(clazz));
		beanDefinition.setScope(getScope(clazz, clazz, Scope.DEFAULT));
		Aop enableAop = AnnotatedElementUtils.getMergedAnnotation(clazz, Aop.class);
		if (enableAop != null) {
			Elements<? extends ReflectionConstructor> executors = beanDefinition.getExecutors();
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
	protected boolean canResolveMethodBeanDefinition(Class<?> clazz) {
		return clazz.isAnnotationPresent(Configuration.class);
	}

	@Override
	protected DefaultBeanDefinition<BeanFactoryExecutor> resolveBeanDefinition(ConfigurableApplicationContext context,
			Class<?> clazz, String originBeanName, BeanDefinition originBeanDefinition, Method method) {
		DefaultBeanDefinition<BeanFactoryExecutor> beanDefinition = super.resolveBeanDefinition(context, clazz,
				originBeanName, originBeanDefinition, method);
		beanDefinition.setSingleton(isSingleton(method));
		beanDefinition.setScope(getScope(clazz, method, originBeanDefinition.getScope()));
		Aop enableAop = AnnotatedElementUtils.getMergedAnnotation(clazz, Aop.class);
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

	protected Scope getScope(Class<?> sourceClass, AnnotatedElement annotatedElement, Scope defaultScope) {
		io.basc.framework.context.annotation.Scope scope = AnnotatedElementUtils.getMergedAnnotation(annotatedElement,
				io.basc.framework.context.annotation.Scope.class);
		if (scope != null) {
			return io.basc.framework.beans.factory.Scope.getFirstOrCreate(scope.value(),
					io.basc.framework.beans.factory.Scope.class,
					() -> new io.basc.framework.beans.factory.Scope(scope.value()));
		}
		return defaultScope;
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
	protected Elements<String> getAliasNames(Class<?> sourceClass, AnnotatedElement annotatedElement) {
		Bean bean = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Bean.class);
		return Elements.forArray(bean.value()).concat(Elements.forArray(bean.name()));
	}

	@Override
	protected Elements<? extends Condition> getConditions(ConfigurableApplicationContext context,
			AnnotatedElement annotatedElement) {
		Conditional conditional = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Conditional.class);
		if (conditional == null) {
			return super.getConditions(context, annotatedElement);
		}

		return Elements.forArray(conditional.value()).flatMap((e) -> context.getBeanProvider(e).getServices());
	}
}
