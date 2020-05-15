package scw.net.http.server.mvc.action;

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
import scw.net.http.HttpMethod;
import scw.net.http.server.mvc.annotation.Filters;
import scw.net.http.server.mvc.annotation.Methods;
import scw.net.http.server.mvc.annotation.Controller;

public class DefaultHttpAction extends BeanAction {
	protected final Set<ControllerDescriptor> controllerDescriptors = new HashSet<ControllerDescriptor>(
			8);
	protected final Set<ControllerDescriptor> targetClassControllerDescriptors = new HashSet<ControllerDescriptor>(
			8);
	protected final Set<ControllerDescriptor> methodControllerDescriptors = new HashSet<ControllerDescriptor>(
			8);
	
	public DefaultHttpAction(BeanFactory beanFactory, Class<?> targetClass,
			Method method, Collection<ActionFilter> actionFilters) {
		super(beanFactory, targetClass, method, actionFilters);
		Controller classController = getTargetClassAnnotatedElement()
				.getAnnotation(Controller.class);
		Controller methodController = getMethodAnnotatedElement()
				.getAnnotation(Controller.class);
		controllerDescriptors.addAll(createControllerDescriptors(
				XUtils.mergePath("/", classController.value(),
						methodController.value()), getControllerHttpMethods()));
		targetClassControllerDescriptors.addAll(createControllerDescriptors(
				XUtils.mergePath("/", classController.value()),
				Arrays.asList(classController.methods())));
		methodControllerDescriptors.addAll(createControllerDescriptors(
				methodController.value(),
				Arrays.asList(methodController.methods())));
		this.actionFilters.addAll(getControllerActionFilters());
	}

	protected Collection<ControllerDescriptor> createControllerDescriptors(
			String controller, Collection<HttpMethod> httpMethods) {
		if (controller == null || CollectionUtils.isEmpty(httpMethods)) {
			return Arrays.asList(new ControllerDescriptor(controller, HttpMethod.GET));
		}
		List<ControllerDescriptor> descriptors = new LinkedList<ControllerDescriptor>();
		for (HttpMethod httpMethod : httpMethods) {
			descriptors.add(new ControllerDescriptor(controller, httpMethod));
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
				for (scw.net.http.HttpMethod requestType : classController
						.methods()) {
					httpMethods.add(requestType);
				}
			}
		} else {
			for (scw.net.http.HttpMethod requestType : methods.value()) {
				httpMethods.add(requestType);
			}
		}

		if (methodController != null) {
			for (scw.net.http.HttpMethod requestType : methodController
					.methods()) {
				httpMethods.add(requestType);
			}
		}

		if (httpMethods.isEmpty()) {
			httpMethods.add(scw.net.http.HttpMethod.GET);
		}
		return httpMethods;
	}
	
	public Set<ControllerDescriptor> getControllerDescriptors() {
		return Collections.unmodifiableSet(controllerDescriptors);
	}

	public Set<ControllerDescriptor> getTargetClassControllerDescriptors() {
		return Collections.unmodifiableSet(targetClassControllerDescriptors);
	}

	public Set<ControllerDescriptor> getMethodControllerDescriptors() {
		return Collections.unmodifiableSet(methodControllerDescriptors);
	}
}
