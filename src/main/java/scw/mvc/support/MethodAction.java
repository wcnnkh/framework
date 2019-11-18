package scw.mvc.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import scw.aop.Invoker;
import scw.beans.BeanFactory;
import scw.beans.MethodProxyInvoker;
import scw.core.PropertyFactory;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.AnnotationFactory;
import scw.core.reflect.AnnotationUtils;
import scw.core.reflect.SimpleAnnotationFactory;
import scw.mvc.AbstractAction;
import scw.mvc.Filter;
import scw.mvc.MVCUtils;
import scw.mvc.ParameterFilter;

public class MethodAction extends AbstractAction {
	private final Invoker invoker;
	private final ParameterConfig[] parameterConfigs;
	private final Collection<ParameterFilter> parameterFilters;
	private final AnnotationFactory superAnnotationFactory;
	private final AnnotationFactory annotationFactory;
	private final Class<?> targetClass;
	private final Method method;
	private final Collection<Filter> filters;

	public MethodAction(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass, Method method,
			AnnotationFactory superAnnotationFactory) {
		this.invoker = new MethodProxyInvoker(beanFactory, targetClass, method);
		this.parameterFilters = MVCUtils.getParameterFilters(beanFactory, propertyFactory);
		parameterFilters.addAll(MVCUtils.getParameterFilters(beanFactory, targetClass, method));
		this.parameterConfigs = ParameterUtils.getParameterConfigs(method);
		this.targetClass = targetClass;
		this.method = method;
		this.superAnnotationFactory = superAnnotationFactory;
		this.annotationFactory = new SimpleAnnotationFactory(method);
		this.filters = MVCUtils.getActionFilter(beanFactory, propertyFactory);
		filters.addAll(MVCUtils.getControllerFilter(targetClass, method, beanFactory));
	}

	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return AnnotationUtils.getAnnotation(type, superAnnotationFactory, annotationFactory);
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

	@Override
	public Collection<Filter> getFilters() {
		return filters;
	}

	@Override
	public Collection<ParameterFilter> getParameterFilters() {
		return parameterFilters;
	}
}
