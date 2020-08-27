package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.parameter.MethodParameterDescriptors;
import scw.core.parameter.ParameterDescriptors;

public abstract class AbstractAction implements Action {
	private final Method method;
	private final Class<?> sourceClass;
	private final AnnotatedElement annotatedElement;
	private final ParameterDescriptors parameterDescriptors;
	protected Collection<ActionInterceptor> actionInterceptor = new LinkedHashSet<ActionInterceptor>(4);

	public AbstractAction(Class<?> sourceClass, Method method) {
		this.sourceClass = sourceClass;
		this.method = method;
		this.annotatedElement = AnnotatedElementUtils.forAnnotations(method.getAnnotations());
		this.parameterDescriptors = new MethodParameterDescriptors(sourceClass, method);
	}

	public void optimization() {
		this.actionInterceptor = Arrays.asList(actionInterceptor.toArray(new ActionInterceptor[0]));
	}

	public Iterable<? extends ActionInterceptor> getActionInterceptors() {
		return Collections.unmodifiableCollection(actionInterceptor);
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

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
