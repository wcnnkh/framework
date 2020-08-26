package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import scw.beans.BeanFactory;
import scw.core.utils.CollectionUtils;
import scw.core.utils.XUtils;
import scw.http.HttpMethod;
import scw.http.server.HttpControllerDescriptor;
import scw.mvc.annotation.ActionInterceptors;
import scw.mvc.annotation.Controller;
import scw.mvc.annotation.Methods;

public class DefaultAction extends BeanAction {
	protected Collection<HttpControllerDescriptor> httpHttpControllerDescriptors = new HashSet<HttpControllerDescriptor>(
			8);
	protected Collection<HttpControllerDescriptor> targetClassHttpControllerDescriptors = new HashSet<HttpControllerDescriptor>(
			8);
	protected Collection<HttpControllerDescriptor> methodHttpControllerDescriptors = new HashSet<HttpControllerDescriptor>(
			8);
	
	public DefaultAction(BeanFactory beanFactory, Class<?> targetClass,
			Method method) {
		super(beanFactory, targetClass, method);
		Controller classController = getTargetClassAnnotatedElement()
				.getAnnotation(Controller.class);
		Controller methodController = getMethodAnnotatedElement()
				.getAnnotation(Controller.class);
		httpHttpControllerDescriptors.addAll(createHttpControllerDescriptors(
				XUtils.mergePath("/", classController.value(),
						methodController.value()), getControllerHttpMethods()));
		targetClassHttpControllerDescriptors.addAll(createHttpControllerDescriptors(
				XUtils.mergePath("/", classController.value()),
				Arrays.asList(classController.methods())));
		methodHttpControllerDescriptors.addAll(createHttpControllerDescriptors(
				methodController.value(),
				Arrays.asList(methodController.methods())));
		this.actionInterceptor.addAll(getControllerActionFilters());
	}
	
	@Override
	public void optimization() {
		this.httpHttpControllerDescriptors = Arrays.asList(httpHttpControllerDescriptors.toArray(new HttpControllerDescriptor[0]));
		this.targetClassHttpControllerDescriptors = Arrays.asList(targetClassHttpControllerDescriptors.toArray(new HttpControllerDescriptor[0]));
		this.methodHttpControllerDescriptors = Arrays.asList(methodHttpControllerDescriptors.toArray(new HttpControllerDescriptor[0]));
		super.optimization();
	}

	protected Collection<HttpControllerDescriptor> createHttpControllerDescriptors(
			String controller, Collection<HttpMethod> httpMethods) {
		if (controller == null || CollectionUtils.isEmpty(httpMethods)) {
			return Arrays.asList(new HttpControllerDescriptor(controller, HttpMethod.GET));
		}
		List<HttpControllerDescriptor> descriptors = new LinkedList<HttpControllerDescriptor>();
		for (HttpMethod httpMethod : httpMethods) {
			descriptors.add(new HttpControllerDescriptor(controller, httpMethod));
		}
		return descriptors;
	}

	protected List<ActionInterceptor> getControllerActionFilters() {
		ActionInterceptors actionInterceptors = getTargetClassAnnotatedElement().getAnnotation(
				ActionInterceptors.class);
		LinkedList<ActionInterceptor> list = new LinkedList<ActionInterceptor>();
		if (actionInterceptors != null) {
			for (Class<? extends ActionInterceptor> f : actionInterceptors.value()) {
				if (ActionInterceptor.class.isAssignableFrom(f)) {
					list.add(getBeanFactory().getInstance(f));
				}
			}
		}

		Controller controller = getTargetClassAnnotatedElement().getAnnotation(
				Controller.class);
		if (controller != null) {
			for (Class<? extends ActionInterceptor> f : controller.interceptors()) {
				if (ActionInterceptor.class.isAssignableFrom(f)) {
					list.add(getBeanFactory().getInstance(f));
				}
			}
		}

		actionInterceptors = getMethodAnnotatedElement().getAnnotation(ActionInterceptors.class);
		if (actionInterceptors != null) {
			list.clear();
			for (Class<? extends ActionInterceptor> f : actionInterceptors.value()) {
				if (ActionInterceptor.class.isAssignableFrom(f)) {
					list.add(getBeanFactory().getInstance(f));
				}
			}
		}

		controller = getMethodAnnotatedElement()
				.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends ActionInterceptor> f : controller.interceptors()) {
				if (ActionInterceptors.class.isAssignableFrom(f)) {
					list.add(getBeanFactory().getInstance(f));
				}
			}
		}
		return list;
	}

	private Collection<HttpMethod> getControllerHttpMethods() {
		Controller classController = getTargetClassAnnotatedElement()
				.getAnnotation(Controller.class);
		Controller methodController = getMethodAnnotatedElement()
				.getAnnotation(Controller.class);
		Methods methods = getMethodAnnotatedElement().getAnnotation(
				Methods.class);
		Set<HttpMethod> httpMethods = new HashSet<HttpMethod>();
		if (methods == null) {
			if (classController != null) {
				for (scw.http.HttpMethod requestType : classController
						.methods()) {
					httpMethods.add(requestType);
				}
			}
		} else {
			for (scw.http.HttpMethod requestType : methods.value()) {
				httpMethods.add(requestType);
			}
		}

		if (methodController != null) {
			for (scw.http.HttpMethod requestType : methodController
					.methods()) {
				httpMethods.add(requestType);
			}
		}

		if (httpMethods.isEmpty()) {
			httpMethods.add(scw.http.HttpMethod.GET);
		}
		return httpMethods;
	}
	
	public Collection<HttpControllerDescriptor> getHttpControllerDescriptors() {
		return httpHttpControllerDescriptors;
	}

	public Collection<HttpControllerDescriptor> getTargetClassHttpControllerDescriptors() {
		return targetClassHttpControllerDescriptors;
	}

	public Collection<HttpControllerDescriptor> getMethodHttpControllerDescriptors() {
		return methodHttpControllerDescriptors;
	}
}
