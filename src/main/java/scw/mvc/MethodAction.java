package scw.mvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;

import scw.core.aop.Invoker;
import scw.core.reflect.EmptyInvocationHandler;
import scw.core.utils.ClassUtils;

public final class MethodAction implements Action {
	private static final ChannelParameterDefinition CHANNEL_PARAMETER_DEFINITION = (ChannelParameterDefinition) Proxy
			.newProxyInstance(ChannelParameterDefinition.class.getClassLoader(),
					new Class<?>[] { ChannelParameterDefinition.class }, new EmptyInvocationHandler());

	public static interface ChannelParameterDefinition extends ParameterDefinition {
	}

	private final Invoker invoker;
	private final ParameterDefinition[] parameterDefinitions;
	private final Collection<ParameterFilter> parameterFilters;

	public MethodAction(Invoker invoker, Collection<ParameterFilter> parameterFilters, Method method) {
		this.invoker = invoker;
		this.parameterFilters = parameterFilters;
		String[] names = ClassUtils.getParameterName(method);
		Annotation[][] parameterAnnoatations = method.getParameterAnnotations();
		Type[] parameterGenericTypes = method.getGenericParameterTypes();
		Class<?>[] parameterTypes = method.getParameterTypes();
		this.parameterDefinitions = new ParameterDefinition[names.length];
		for (int i = 0; i < names.length; i++) {
			if (Channel.class.isAssignableFrom(parameterTypes[i])) {
				parameterDefinitions[i] = CHANNEL_PARAMETER_DEFINITION;
			} else {
				parameterDefinitions[i] = new SimpleParameterDefinition(names.length, names[i],
						parameterAnnoatations[i], parameterTypes[i], parameterGenericTypes[i], i);
			}
		}
	}

	public void doAction(Channel channel) throws Throwable {
		Object[] args = new Object[parameterDefinitions.length];
		for (int i = 0; i < parameterDefinitions.length; i++) {
			ParameterDefinition parameterDefinition = parameterDefinitions[i];
			if (parameterDefinition == CHANNEL_PARAMETER_DEFINITION) {
				args[i] = channel;
			} else {
				ParameterFilterChain parameterFilterChain = new SimpleParameterParseFilterChain(parameterFilters);
				args[i] = parameterFilterChain.doFilter(channel, parameterDefinitions[i]);
			}
		}

		Object rtn = invoker.invoke(args);
		channel.write(rtn);
	}
}
