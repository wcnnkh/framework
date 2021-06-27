package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import scw.convert.TypeDescriptor;
import scw.core.ResolvableType;
import scw.core.annotation.AnnotatedElementWrapper;
import scw.core.annotation.AnnotationArrayAnnotatedElement;
import scw.core.annotation.MultiAnnotatedElement;
import scw.core.parameter.MethodParameterDescriptors;
import scw.core.parameter.ParameterDescriptors;
import scw.mvc.HttpPatternResolver;
import scw.web.pattern.HttpPattern;

public abstract class AbstractAction extends
		AnnotatedElementWrapper<AnnotatedElement> implements Action {
	private Collection<HttpPattern> httpPatterns;
	private final Method method;
	private final Class<?> sourceClass;
	private final ParameterDescriptors parameterDescriptors;
	private final TypeDescriptor returnType;

	public AbstractAction(Class<?> sourceClass, Method method,
			HttpPatternResolver httpPatternResolver) {
		super(new AnnotationArrayAnnotatedElement(method));
		this.returnType = new TypeDescriptor(
				ResolvableType.forMethodReturnType(method),
				method.getReturnType(), MultiAnnotatedElement
						.forAnnotatedElements(this, sourceClass)
						.getAnnotations());
		this.sourceClass = sourceClass;
		this.method = method;
		this.parameterDescriptors = new MethodParameterDescriptors(sourceClass,
				method);
		this.httpPatterns = httpPatternResolver.resolveHttpPattern(sourceClass,
				method);
	}

	@Override
	public TypeDescriptor getReturnType() {
		return returnType;
	}

	public void optimization() {
		this.httpPatterns = Arrays.asList(httpPatterns
				.toArray(new HttpPattern[0]));
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

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
