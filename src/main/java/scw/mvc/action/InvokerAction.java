package scw.mvc.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.beans.MethodProxyInvoker;
import scw.core.PropertyFactory;
import scw.core.aop.Invoker;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.AnnotationFactory;
import scw.core.reflect.AnnotationUtils;
import scw.core.reflect.SimpleAnnotationFactory;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.parameter.ParameterFilter;

public class InvokerAction implements MethodAction {
	private final Invoker invoker;
	private final ParameterConfig[] parameterConfigs;
	private final Collection<ParameterFilter> parameterFilters;
	private final AnnotationFactory superAnnotationFactory;
	private final AnnotationFactory annotationFactory;
	private final Class<?> targetClass;
	private final Method method;

	public InvokerAction(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			Method method, AnnotationFactory superAnnotationFactory) {
		this.invoker = new MethodProxyInvoker(beanFactory, targetClass, method);
		this.parameterFilters = MVCUtils.getParameterFilters(beanFactory, targetClass, method);
		this.parameterConfigs = ParameterUtils.getParameterConfigs(method);
		this.targetClass = targetClass;
		this.method = method;
		this.superAnnotationFactory = superAnnotationFactory;
		this.annotationFactory = new SimpleAnnotationFactory(method);
	}

	public Object doAction(Channel channel) throws Throwable {
		Object[] args = MVCUtils.getParameterValues(channel, parameterConfigs, parameterFilters);
		return invoker.invoke(args);
	}

	@Override
	public String toString() {
		return invoker.toString();
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
}
