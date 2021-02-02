package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.parameter.MethodParameterDescriptors;
import scw.core.parameter.ParameterDescriptors;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.http.HttpMethod;
import scw.http.server.HttpControllerDescriptor;
import scw.mvc.annotation.Controller;
import scw.mvc.annotation.Methods;
import scw.util.placeholder.PropertyResolver;

public abstract class AbstractAction implements Action {
	protected Collection<HttpControllerDescriptor> httpHttpControllerDescriptors = new HashSet<HttpControllerDescriptor>(
			8);
	protected Collection<HttpControllerDescriptor> sourceClassHttpControllerDescriptors = new HashSet<HttpControllerDescriptor>(
			8);
	protected Collection<HttpControllerDescriptor> methodHttpControllerDescriptors = new HashSet<HttpControllerDescriptor>(
			8);
	
	private final Method method;
	private final Class<?> sourceClass;
	private final AnnotatedElement annotatedElement;
	private final ParameterDescriptors parameterDescriptors;

	public AbstractAction(Class<?> sourceClass, Method method, PropertyResolver propertyResolver) {
		this.sourceClass = sourceClass;
		this.method = method;
		this.annotatedElement = AnnotatedElementUtils.forAnnotations(method.getAnnotations());
		this.parameterDescriptors = new MethodParameterDescriptors(sourceClass, method);
		
		Controller classController = getSourceClass()
				.getAnnotation(Controller.class);
		Controller methodController = getAnnotatedElement()
				.getAnnotation(Controller.class);
		
		String controller = classController.value();
		controller = propertyResolver.resolvePlaceholders(controller);
		
		String methodControllerValue = methodController.value();
		methodControllerValue = propertyResolver.resolvePlaceholders(methodControllerValue);
		
		httpHttpControllerDescriptors.addAll(createHttpControllerDescriptors(
				StringUtils.mergePath("/", controller, methodControllerValue), getControllerHttpMethods()));
		sourceClassHttpControllerDescriptors.addAll(createHttpControllerDescriptors(
				StringUtils.mergePath("/", controller),
				Arrays.asList(classController.methods())));
		methodHttpControllerDescriptors.addAll(createHttpControllerDescriptors(
				methodControllerValue, Arrays.asList(methodController.methods())));
	}

	public void optimization() {
		this.httpHttpControllerDescriptors = Arrays.asList(httpHttpControllerDescriptors.toArray(new HttpControllerDescriptor[0]));
		this.sourceClassHttpControllerDescriptors = Arrays.asList(sourceClassHttpControllerDescriptors.toArray(new HttpControllerDescriptor[0]));
		this.methodHttpControllerDescriptors = Arrays.asList(methodHttpControllerDescriptors.toArray(new HttpControllerDescriptor[0]));
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public Method getMethod() {
		return method;
	}

	public AnnotatedElement getAnnotatedElement() {
		return annotatedElement;
	}

	public ParameterDescriptors getParameterDescriptors() {
		return parameterDescriptors;
	}

	@Override
	public final int hashCode() {
		return method.hashCode();
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Action) {
			return getMethod().equals(((Action) obj).getMethod());
		}
		return false;
	}
	
	public Collection<HttpControllerDescriptor> getHttpControllerDescriptors() {
		return httpHttpControllerDescriptors;
	}

	public Collection<HttpControllerDescriptor> getSourceClassHttpControllerDescriptors() {
		return sourceClassHttpControllerDescriptors;
	}

	public Collection<HttpControllerDescriptor> getMethodHttpControllerDescriptors() {
		return methodHttpControllerDescriptors;
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

	private Collection<HttpMethod> getControllerHttpMethods() {
		Controller classController = getSourceClass()
				.getAnnotation(Controller.class);
		Controller methodController = getAnnotatedElement()
				.getAnnotation(Controller.class);
		Methods methods = getAnnotatedElement().getAnnotation(
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

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
