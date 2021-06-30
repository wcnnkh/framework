package scw.mvc.annotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.http.HttpMethod;
import scw.mvc.HttpPatternResolver;
import scw.util.placeholder.PropertyResolver;
import scw.util.placeholder.PropertyResolverAware;
import scw.web.pattern.HttpPattern;

public class AnnotationHttpPatternResolver implements HttpPatternResolver, PropertyResolverAware{
	private PropertyResolver propertyResolver;
	
	public PropertyResolver getPropertyResolver() {
		return propertyResolver;
	}

	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}
	
	public boolean canResolveHttpPattern(Class<?> clazz){
		return clazz.isAnnotationPresent(Controller.class);
	}

	@Override
	public boolean canResolveHttpPattern(Class<?> clazz, Method method) {
		return clazz.isAnnotationPresent(Controller.class)
				&& method.isAnnotationPresent(Controller.class);
	}

	@Override
	public Collection<HttpPattern> resolveHttpPattern(Class<?> clazz,
			Method method) {
		Collection<HttpPattern> httpPatterns = new HashSet<HttpPattern>(8);
		Controller classController = clazz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);

		String controller = classController.value();
		if (propertyResolver != null) {
			controller = propertyResolver.resolvePlaceholders(controller);
		}

		String methodControllerValue = methodController.value();
		if (propertyResolver != null) {
			methodControllerValue = propertyResolver
					.resolvePlaceholders(methodControllerValue);
		}
		httpPatterns.addAll(createHttpControllerDescriptors(
				StringUtils.mergePath("/", controller, methodControllerValue),
				getControllerHttpMethods(clazz, method)));
		return httpPatterns;
	}

	protected Collection<HttpPattern> createHttpControllerDescriptors(
			String controller, Collection<HttpMethod> httpMethods) {
		if (controller == null || CollectionUtils.isEmpty(httpMethods)) {
			return Arrays.asList(new HttpPattern(controller, HttpMethod.GET.name()));
		}
		List<HttpPattern> descriptors = new LinkedList<HttpPattern>();
		for (HttpMethod httpMethod : httpMethods) {
			descriptors.add(new HttpPattern(controller, httpMethod.name()));
		}
		return descriptors;
	}

	private Collection<HttpMethod> getControllerHttpMethods(Class<?> clazz,
			Method method) {
		Controller classController = clazz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		Methods methods = method.getAnnotation(Methods.class);
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
			for (scw.http.HttpMethod requestType : methodController.methods()) {
				httpMethods.add(requestType);
			}
		}

		if (httpMethods.isEmpty()) {
			httpMethods.add(scw.http.HttpMethod.GET);
		}
		return httpMethods;
	}
}
