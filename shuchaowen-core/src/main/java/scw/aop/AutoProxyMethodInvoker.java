package scw.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import scw.core.instance.NoArgsInstanceFactory;

public class AutoProxyMethodInvoker extends ProxyMethodInvoker {
	private static final long serialVersionUID = 1L;
	private final NoArgsInstanceFactory instanceFactory;
	private final String id;
	private final Method method;

	public AutoProxyMethodInvoker(NoArgsInstanceFactory instanceFactory, Class<?> targetClass, Method method,
			Collection<String> filterNames) {
		this(instanceFactory, targetClass, method, targetClass.getName(), null,
				new InstanceFactoryFilterChain(instanceFactory, filterNames, null));
	}

	public AutoProxyMethodInvoker(NoArgsInstanceFactory instanceFactory, Class<?> targetClass, Method method, String id,
			Collection<String> filterNames) {
		this(instanceFactory, targetClass, method, id, null,
				new InstanceFactoryFilterChain(instanceFactory, filterNames, null));
	}

	public AutoProxyMethodInvoker(NoArgsInstanceFactory instanceFactory, Class<?> targetClass, Method method,
			Collection<Filter> filters, FilterChain filterChain) {
		this(instanceFactory, targetClass, method, targetClass.getName(), filters, filterChain);
	}

	public AutoProxyMethodInvoker(NoArgsInstanceFactory instanceFactory, Class<?> targetClass, Method method, String id,
			Collection<Filter> filters, FilterChain filterChain) {
		super(targetClass, filters, filterChain);
		this.id = id;
		this.method = method;
		this.instanceFactory = instanceFactory;
	}

	@Override
	protected Object getInstance() {
		return Modifier.isStatic(getMethod().getModifiers()) ? null : instanceFactory.getInstance(id);
	}

	@Override
	protected Method getMethod() {
		return this.method;
	}

}
