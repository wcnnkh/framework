package scw.mvc.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import scw.aop.Invoker;
import scw.beans.AutoProxyMethodInvoker;
import scw.beans.BeanFactory;
import scw.core.annotation.AnnotationFactory;
import scw.core.annotation.AnnotationUtils;
import scw.core.annotation.SimpleAnnotationFactory;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.ParameterUtils;
import scw.mvc.MVCUtils;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.parameter.ParameterFilter;
import scw.util.value.property.PropertyFactory;

public abstract class MethodAction extends AbstractAction {
	private final Invoker invoker;
	private final ParameterConfig[] parameterConfigs;
	private final Collection<ParameterFilter> parameterFilters;
	private final AnnotationFactory superAnnotationFactory;
	private final AnnotationFactory annotationFactory;
	private final Class<?> targetClass;
	private final Method method;
	private final Collection<ActionFilter> filters;

	public MethodAction(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass,
			Method method, AnnotationFactory superAnnotationFactory) {
		this.invoker = new AutoProxyMethodInvoker(beanFactory, targetClass,
				method);
		this.parameterFilters = MVCUtils.getParameterFilters(beanFactory,
				propertyFactory);
		parameterFilters.addAll(MVCUtils.getParameterFilters(beanFactory,
				targetClass, method));
		this.parameterConfigs = ParameterUtils.getParameterConfigs(method);
		this.targetClass = targetClass;
		this.method = method;
		this.superAnnotationFactory = superAnnotationFactory;
		this.annotationFactory = new SimpleAnnotationFactory(method);
		this.filters = MVCUtils.getActionFilters(targetClass, method,
				beanFactory);
	}

	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return AnnotationUtils.getAnnotation(type, superAnnotationFactory,
				annotationFactory);
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public Method getMethod() {
		return method;
	}

	public ParameterConfig[] getParameterConfigs() {
		return parameterConfigs;
	}

	@Override
	public Invoker getInvoker() {
		return invoker;
	}

	public Collection<ActionFilter> getActionFilters() {
		return filters;
	}
	
	@Override
	public Collection<ParameterFilter> getParameterFilters() {
		return parameterFilters;
	}
}
