package scw.mvc.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import scw.core.PropertyFactory;
import scw.core.aop.Invoker;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.parameter.ContainAnnotationParameterConfig;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.AnnotationFactory;
import scw.core.reflect.AnnotationUtils;
import scw.core.reflect.SimpleAnnotationFactory;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.parameter.ParameterFilter;

public class InvokerAction implements MethodAction {
	private final Invoker invoker;
	private final ContainAnnotationParameterConfig[] containAnnotationParameterConfigs;
	private final Collection<ParameterFilter> parameterFilters;
	private final AnnotationFactory superAnnotationFactory;
	private final AnnotationFactory annotationFactory;
	private final Class<?> targetClass;
	private final Method method;

	public InvokerAction(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			Method method, AnnotationFactory superAnnotationFactory) {
		this.invoker = InstanceUtils.getInvoker(instanceFactory, targetClass, method);
		this.parameterFilters = MVCUtils.getParameterFilters(instanceFactory, targetClass, method);
		this.containAnnotationParameterConfigs = ParameterUtils.getParameterConfigs(method);
		this.targetClass = targetClass;
		this.method = method;
		this.superAnnotationFactory = superAnnotationFactory;
		this.annotationFactory = new SimpleAnnotationFactory(method);
	}

	public Object doAction(Channel channel) throws Throwable {
		Object[] args = MVCUtils.getParameterValues(channel, containAnnotationParameterConfigs, parameterFilters);
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
}
