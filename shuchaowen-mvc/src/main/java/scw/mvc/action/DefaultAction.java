package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import scw.beans.BeanFactory;
import scw.core.utils.CollectionUtils;
import scw.core.utils.XUtils;
import scw.http.HttpMethod;
import scw.http.server.HttpControllerDescriptor;
import scw.mvc.annotation.Controller;
import scw.mvc.annotation.Filters;
import scw.mvc.annotation.Methods;

public class DefaultAction extends BeanAction {
	protected final Set<HttpControllerDescriptor> httpHttpControllerDescriptors = new HashSet<HttpControllerDescriptor>(
			8);
	protected final Set<HttpControllerDescriptor> targetClassHttpControllerDescriptors = new HashSet<HttpControllerDescriptor>(
			8);
	protected final Set<HttpControllerDescriptor> methodHttpControllerDescriptors = new HashSet<HttpControllerDescriptor>(
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
		this.actionFilters.addAll(getControllerActionFilters());
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

	protected List<ActionFilter> getControllerActionFilters() {
		Filters filters = getTargetClassAnnotatedElement().getAnnotation(
				Filters.class);
		LinkedList<ActionFilter> list = new LinkedList<ActionFilter>();
		if (filters != null) {
			for (Class<? extends ActionFilter> f : filters.value()) {
				if (ActionFilter.class.isAssignableFrom(f)) {
					list.add(getBeanFactory().getInstance(f));
				}
			}
		}

		Controller controller = getTargetClassAnnotatedElement().getAnnotation(
				Controller.class);
		if (controller != null) {
			for (Class<? extends ActionFilter> f : controller.filters()) {
				if (ActionFilter.class.isAssignableFrom(f)) {
					list.add(getBeanFactory().getInstance(f));
				}
			}
		}

		filters = getMethodAnnotatedElement().getAnnotation(Filters.class);
		if (filters != null) {
			list.clear();
			for (Class<? extends ActionFilter> f : filters.value()) {
				if (ActionFilter.class.isAssignableFrom(f)) {
					list.add(getBeanFactory().getInstance(f));
				}
			}
		}

		controller = getMethodAnnotatedElement()
				.getAnnotation(Controller.class);
		if (controller != null) {
			for (Class<? extends ActionFilter> f : controller.filters()) {
				if (ActionFilter.class.isAssignableFrom(f)) {
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
	
	public Set<HttpControllerDescriptor> getHttpControllerDescriptors() {
		return Collections.unmodifiableSet(httpHttpControllerDescriptors);
	}

	public Set<HttpControllerDescriptor> getTargetClassHttpControllerDescriptors() {
		return Collections.unmodifiableSet(targetClassHttpControllerDescriptors);
	}

	public Set<HttpControllerDescriptor> getMethodHttpControllerDescriptors() {
		return Collections.unmodifiableSet(methodHttpControllerDescriptors);
	}
}
