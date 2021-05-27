package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import scw.convert.TypeDescriptor;
import scw.core.ResolvableType;
import scw.core.annotation.AnnotatedElementWrapper;
import scw.core.annotation.AnnotationArrayAnnotatedElement;
import scw.core.annotation.MultiAnnotatedElement;
import scw.core.parameter.MethodParameterDescriptors;
import scw.core.parameter.ParameterDescriptors;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.http.HttpMethod;
import scw.mvc.annotation.Controller;
import scw.mvc.annotation.Methods;
import scw.util.placeholder.PropertyResolver;
import scw.web.pattern.HttpPattern;

public abstract class AbstractAction extends AnnotatedElementWrapper<AnnotatedElement> implements Action {
	protected Collection<HttpPattern> httpPatterns = new HashSet<HttpPattern>(8);

	private final Method method;
	private final Class<?> sourceClass;
	private final ParameterDescriptors parameterDescriptors;
	private final TypeDescriptor returnType;

	public AbstractAction(Class<?> sourceClass, Method method, PropertyResolver propertyResolver) {
		super(new AnnotationArrayAnnotatedElement(method));
		this.returnType = new TypeDescriptor(ResolvableType.forMethodReturnType(method), method.getReturnType(),
				MultiAnnotatedElement.forAnnotatedElements(this, sourceClass).getAnnotations());
		this.sourceClass = sourceClass;
		this.method = method;
		this.parameterDescriptors = new MethodParameterDescriptors(sourceClass, method);

		Controller classController = getDeclaringClass().getAnnotation(Controller.class);
		Controller methodController = getMethod().getAnnotation(Controller.class);

		String controller = classController.value();
		controller = propertyResolver.resolvePlaceholders(controller);

		String methodControllerValue = methodController.value();
		methodControllerValue = propertyResolver.resolvePlaceholders(methodControllerValue);
		httpPatterns.addAll(createHttpControllerDescriptors(
				StringUtils.mergePath("/", controller, methodControllerValue), getControllerHttpMethods()));
	}

	@Override
	public TypeDescriptor getReturnType() {
		return returnType;
	}

	public void optimization() {
		this.httpPatterns = Arrays.asList(httpPatterns.toArray(new HttpPattern[0]));
	}

	public Class<?> getDeclaringClass() {
		return sourceClass;
	}

	public Method getMethod() {
		return method;
	}

	public ParameterDescriptors getParameterDescriptors() {
		return parameterDescriptors;
	}

	@Override
	public Collection<HttpPattern> getPatternts() {
		return httpPatterns;
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

	protected Collection<HttpPattern> createHttpControllerDescriptors(String controller,
			Collection<HttpMethod> httpMethods) {
		if (controller == null || CollectionUtils.isEmpty(httpMethods)) {
			return Arrays.asList(new HttpPattern(controller, HttpMethod.GET));
		}
		List<HttpPattern> descriptors = new LinkedList<HttpPattern>();
		for (HttpMethod httpMethod : httpMethods) {
			descriptors.add(new HttpPattern(controller, httpMethod));
		}
		return descriptors;
	}

	private Collection<HttpMethod> getControllerHttpMethods() {
		Controller classController = getDeclaringClass().getAnnotation(Controller.class);
		Controller methodController = getMethod().getAnnotation(Controller.class);
		Methods methods = getMethod().getAnnotation(Methods.class);
		Set<HttpMethod> httpMethods = new HashSet<HttpMethod>();
		if (methods == null) {
			if (classController != null) {
				for (scw.http.HttpMethod requestType : classController.methods()) {
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

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
