package io.basc.framework.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.annotation.AnnotatedElementWrapper;
import io.basc.framework.core.annotation.AnnotationArrayAnnotatedElement;
import io.basc.framework.core.annotation.MultiAnnotatedElement;
import io.basc.framework.core.parameter.ExecutableParameterDescriptors;
import io.basc.framework.core.parameter.ParameterDescriptors;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.web.pattern.HttpPattern;
import io.basc.framework.web.pattern.HttpPatternResolver;

public abstract class AbstractAction extends AnnotatedElementWrapper<AnnotatedElement> implements Action {
	private Collection<HttpPattern> httpPatterns;
	private final Method method;
	private final Class<?> sourceClass;
	private final ParameterDescriptors parameterDescriptors;
	private final TypeDescriptor returnType;

	public AbstractAction(Class<?> sourceClass, Method method, HttpPatternResolver httpPatternResolver) {
		super(new AnnotationArrayAnnotatedElement(method));
		this.returnType = new TypeDescriptor(ResolvableType.forMethodReturnType(method), method.getReturnType(),
				MultiAnnotatedElement.forAnnotatedElements(method.getAnnotatedReturnType(), this, sourceClass));
		this.sourceClass = sourceClass;
		this.method = method;
		this.parameterDescriptors = new ExecutableParameterDescriptors(sourceClass, method);
		this.httpPatterns = httpPatternResolver.resolve(sourceClass, method);
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
		int code = 0;
		for (HttpPattern pattern : httpPatterns) {
			code += pattern.hashCode();
		}
		return code;
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
			return ObjectUtils.equals(this.httpPatterns, ((Action) obj).getPatternts());
		}
		return false;
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
