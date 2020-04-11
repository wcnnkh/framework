package scw.mvc.action.http;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumSet;

import scw.beans.BeanFactory;
import scw.core.utils.StringUtils;
import scw.mvc.action.AnnotationAction;
import scw.mvc.annotation.Controller;
import scw.mvc.annotation.HttpAuthorityConfig;
import scw.mvc.annotation.HttpAuthorityParent;
import scw.mvc.annotation.Methods;
import scw.security.authority.http.HttpAuthority;
import scw.security.authority.http.SimpleHttpAuthority;
import scw.util.value.property.PropertyFactory;

public class HttpAnnotationAction extends AnnotationAction implements
		HttpAction {
	private SimpleHttpAuthority authority;
	private EnumSet<scw.net.http.HttpMethod> httpMethods = EnumSet
			.noneOf(scw.net.http.HttpMethod.class);

	public HttpAnnotationAction(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> clz, Method method) {
		super(beanFactory, propertyFactory, clz, method);
		Controller classController = clz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		Methods methods = method.getAnnotation(Methods.class);
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

		HttpAuthorityConfig httpAuthorityConfig = method
				.getAnnotation(HttpAuthorityConfig.class);
		if (httpAuthorityConfig != null) {
			for (scw.net.http.HttpMethod httpMethod : httpMethods) {
				HttpAuthorityParent parent = clz
						.getAnnotation(HttpAuthorityParent.class);
				HttpAuthorityParent methodParent = method
						.getAnnotation(HttpAuthorityParent.class);
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
				authority.setHttpMethod(httpMethod);
				authority.setPath(getController());
				authority.setName(httpAuthorityConfig.name());
				break;
			}
		}
	}

	public HttpAuthority getAuthority() {
		return authority;
	}

	public Collection<scw.net.http.HttpMethod> getHttpMethods() {
		return httpMethods;
	}
}
