package scw.mvc.action;

import java.lang.reflect.Method;

import scw.beans.BeanFactory;
import scw.core.annotation.AnnotationFactory;
import scw.core.utils.XUtils;
import scw.mvc.annotation.Controller;
import scw.util.value.property.PropertyFactory;

public class AnnotationAction extends MethodAction {
	private final String controller;
	private final String classContrller;
	private final String methodController;

	public AnnotationAction(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass,
			Method method, AnnotationFactory superAnnotationFactory) {
		super(beanFactory, propertyFactory, targetClass, method,
				superAnnotationFactory);
		Controller classController = targetClass
				.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		this.classContrller = XUtils.mergePath("/", classController.value());
		this.methodController = methodController.value();
		this.controller = XUtils.mergePath("/", classController.value(),
				methodController.value());
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
}
