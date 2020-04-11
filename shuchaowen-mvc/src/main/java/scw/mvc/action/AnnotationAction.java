package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.utils.XUtils;
import scw.mvc.MVCUtils;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.annotation.Controller;
import scw.mvc.parameter.ParameterFilter;
import scw.util.value.property.PropertyFactory;

public class AnnotationAction extends BeanAction {
	private final String controller;
	private final String classContrller;
	private final String methodController;
	private final Collection<ParameterFilter> parameterFilters;
	private final Collection<ActionFilter> filters;

	public AnnotationAction(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass,
			Method method) {
		super(beanFactory, targetClass, method);
		Controller classController = targetClass
				.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		this.classContrller = XUtils.mergePath("/", classController.value());
		this.methodController = methodController.value();
		this.controller = XUtils.mergePath("/", classController.value(),
				methodController.value());
		this.parameterFilters = MVCUtils.getParameterFilters(beanFactory,
				propertyFactory);
		parameterFilters.addAll(MVCUtils.getParameterFilters(beanFactory,
				targetClass, method));
		this.filters = MVCUtils.getActionFilters(targetClass, method,
				beanFactory);
	}

	public String getController() {
		return controller;
	}

	public String getClassController() {
		return classContrller;
	}

	public String getMethodController() {
		return methodController;
	}
	

	public Collection<ActionFilter> getActionFilters() {
		return filters;
	}

	public Collection<ParameterFilter> getParameterFilters() {
		return parameterFilters;
	}
}
