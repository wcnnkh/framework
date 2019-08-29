package scw.servlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.core.aop.Invoker;
import scw.core.reflect.EmptyInvocationHandler;
import scw.core.utils.ClassUtils;
import scw.mvc.ParameterDefinition;
import scw.mvc.SimpleParameterDefinition;

public class SimpleAction implements Action {
	public static final RequestParameterDefinition REQUEST_PARAMETER_DEFINITION = (RequestParameterDefinition) Proxy
			.newProxyInstance(ResponseParameterDefinition.class.getClassLoader(),
					new Class<?>[] { RequestParameterDefinition.class }, new EmptyInvocationHandler());
	public static final ResponseParameterDefinition RESPONSE_PARAMETER_DEFINITION = (ResponseParameterDefinition) Proxy
			.newProxyInstance(ResponseParameterDefinition.class.getClassLoader(),
					new Class<?>[] { ResponseParameterDefinition.class }, new EmptyInvocationHandler());

	public static interface ResponseParameterDefinition extends ParameterDefinition {
	}

	public static interface RequestParameterDefinition extends ParameterDefinition {
	}

	private final Invoker invoker;
	private final Collection<ParameterFilter> parameterFilters;
	private final ParameterDefinition[] parameterDefinitions;

	public SimpleAction(Invoker invoker, Collection<ParameterFilter> parameterFilters, Method method) {
		this.invoker = invoker;
		this.parameterFilters = parameterFilters;
		String[] names = ClassUtils.getParameterName(method);
		Annotation[][] parameterAnnoatations = method.getParameterAnnotations();
		Type[] parameterGenericTypes = method.getGenericParameterTypes();
		Class<?>[] parameterTypes = method.getParameterTypes();
		this.parameterDefinitions = new ParameterDefinition[names.length];
		for (int i = 0; i < names.length; i++) {
			if (ServletRequest.class.isAssignableFrom(parameterTypes[i])) {
				parameterDefinitions[i] = REQUEST_PARAMETER_DEFINITION;
			} else if (ServletResponse.class.isAssignableFrom(parameterTypes[i])) {
				parameterDefinitions[i] = RESPONSE_PARAMETER_DEFINITION;
			} else {
				parameterDefinitions[i] = new SimpleParameterDefinition(names.length, names[i],
						parameterAnnoatations[i], parameterTypes[i], parameterGenericTypes[i], i);
			}
		}
	}

	public void doAction(Request request, Response response) throws Throwable {
		Object[] args = new Object[parameterDefinitions.length];
		for (int i = 0; i < parameterDefinitions.length; i++) {
			ParameterDefinition parameterDefinition = parameterDefinitions[i];
			if (parameterDefinition == REQUEST_PARAMETER_DEFINITION) {
				args[i] = request;
			} else if (parameterDefinition == RESPONSE_PARAMETER_DEFINITION) {
				args[i] = response;
			} else {
				ParameterFilterChain parameterFilterChain = new DefaultParameterParseFilterChain(parameterFilters);
				args[i] = parameterFilterChain.doFilter(request, parameterDefinitions[i]);
			}
		}

		Object rtn = invoker.invoke(args);
		response.write(rtn);
	}

	@Override
	public String toString() {
		return invoker.toString();
	}
}
