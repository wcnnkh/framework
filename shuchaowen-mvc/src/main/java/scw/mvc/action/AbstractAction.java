package scw.mvc.action;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import scw.aop.Invoker;
import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.MultiAnnotatedElement;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.CollectionUtils;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.action.filter.ActionFilterChain;
import scw.mvc.action.filter.IteratorActionFilterChain;

public abstract class AbstractAction implements Action {
	private final Method method;
	private final Class<?> targetClass;
	private final AnnotatedElement annotatedElement;
	private final AnnotatedElement targetClassAnnotatedElement;
	private final AnnotatedElement methodAnnotatedElement;
	private final ParameterDescriptor[] parameterDescriptors;
	protected final Set<ActionFilter> actionFilters = new LinkedHashSet<ActionFilter>(
			4);

	public AbstractAction(Class<?> targetClass, Method method) {
		this.targetClass = targetClass;
		this.method = method;
		this.targetClassAnnotatedElement = AnnotatedElementUtils
				.forAnnotations(targetClass.getDeclaredAnnotations());
		this.methodAnnotatedElement = AnnotatedElementUtils
				.forAnnotations(method.getAnnotations());
		this.annotatedElement = new MultiAnnotatedElement(Arrays.asList(
				methodAnnotatedElement, targetClassAnnotatedElement));
		this.parameterDescriptors = ParameterUtils
				.getParameterDescriptors(method);
	}

	public AnnotatedElement getAnnotatedElement() {
		return annotatedElement;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public AnnotatedElement getTargetClassAnnotatedElement() {
		return targetClassAnnotatedElement;
	}

	public Method getMethod() {
		return method;
	}

	public AnnotatedElement getMethodAnnotatedElement() {
		return methodAnnotatedElement;
	}

	public ParameterDescriptor[] getParameterDescriptors() {
		return parameterDescriptors;
	}

	public abstract Invoker getInvoker();

	public Collection<ActionFilter> getActionFilters() {
		return Collections.unmodifiableCollection(actionFilters);
	}

	public ActionFilterChain getActionFilterChain() {
		Collection<ActionFilter> filters = getActionFilters();
		return CollectionUtils.isEmpty(filters) ? null
				: new IteratorActionFilterChain(filters, null);
	}

	public Object doAction(Channel channel) throws Throwable {
		return getInvoker()
				.invoke(MVCUtils.getParameterValues(channel,
						getParameterDescriptors()));
	}

	@Override
	public int hashCode() {
		return method.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Action) {
			return ((Action) obj).getMethod().equals(getMethod());
		}
		return false;
	}

	@Override
	public String toString() {
		return getInvoker().toString();
	}
}
