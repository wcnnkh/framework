package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.mvc.annotation.Controller;
import scw.mvc.annotation.Methods;

public class SimpleHttpAction extends MethodAction implements HttpAction {
	private Collection<HttpControllerConfig> httpControllerConfigs = new LinkedList<HttpControllerConfig>();

	public SimpleHttpAction(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> clz, Method method) {
		super(beanFactory, propertyFactory, clz, method);
		Controller classController = clz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		Methods methods = method.getAnnotation(Methods.class);
		Set<String> methodSet = new HashSet<String>();
		if (methods == null) {
			if (classController != null) {
				for (scw.net.http.Method requestType : classController.methods()) {
					methodSet.add(requestType.name());
				}
			}
		} else {
			for (scw.net.http.Method requestType : methods.value()) {
				methodSet.add(requestType.name());
			}
		}

		if (methodController != null) {
			for (scw.net.http.Method requestType : methodController.methods()) {
				methodSet.add(requestType.name());
			}
		}

		if (methodSet.isEmpty()) {
			methodSet.add(scw.net.http.Method.GET.name());
		}

		for (String httpMethod : methodSet) {
			httpControllerConfigs
					.add(new SimpleHttpControllerConfig(classController.value(), methodController.value(), httpMethod));
		}
	}

	public Collection<HttpControllerConfig> getControllerConfigs() {
		return Collections.unmodifiableCollection(httpControllerConfigs);
	}
}
