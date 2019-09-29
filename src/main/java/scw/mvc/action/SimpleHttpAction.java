package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import scw.core.PropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.core.utils.StringUtils;
import scw.mvc.annotation.Controller;
import scw.mvc.annotation.HttpAuthorityConfig;
import scw.mvc.annotation.HttpAuthorityParent;
import scw.mvc.annotation.Methods;
import scw.security.authority.http.HttpAuthority;
import scw.security.authority.http.SimpleHttpAuthority;

public class SimpleHttpAction extends MethodAction implements HttpAction {
	private Collection<HttpControllerConfig> httpControllerConfigs = new LinkedList<HttpControllerConfig>();
	private SimpleHttpAuthority authority;

	public SimpleHttpAction(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> clz, Method method) {
		super(instanceFactory, propertyFactory, clz, method);
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

		HttpAuthorityConfig httpAuthorityConfig = method.getAnnotation(HttpAuthorityConfig.class);
		if (httpAuthorityConfig != null) {
			Iterator<HttpControllerConfig> iterator = httpControllerConfigs.iterator();
			if (iterator.hasNext()) {
				HttpControllerConfig httpControllerConfig = iterator.next();
				HttpAuthorityParent parent = clz.getAnnotation(HttpAuthorityParent.class);
				HttpAuthorityParent methodParent = method.getAnnotation(HttpAuthorityParent.class);
				if (methodParent != null) {
					parent = methodParent;
				}

				String parentId = httpAuthorityConfig.parentId();
				if (parent != null && StringUtils.isEmpty(parentId)) {
					parentId = parent.value();
				}

				authority = new SimpleHttpAuthority();
				authority.setId(httpAuthorityConfig.id());
				authority.setParentId(parentId);
				authority.setMethod(httpControllerConfig.getMethod());
				authority.setPath(httpControllerConfig.getController());
				authority.setName(httpAuthorityConfig.name());
			}
		}
	}

	public Collection<HttpControllerConfig> getControllerConfigs() {
		return Collections.unmodifiableCollection(httpControllerConfigs);
	}

	public HttpAuthority getAuthority() {
		return authority;
	}
}
