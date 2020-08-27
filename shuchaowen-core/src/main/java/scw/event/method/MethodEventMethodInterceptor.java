package scw.event.method;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptorAccept;
import scw.aop.MethodInterceptorChain;
import scw.aop.MethodInvoker;
import scw.core.instance.annotation.Configuration;
import scw.event.method.annotation.PublishMethodEvent;

@Configuration(order = Integer.MIN_VALUE)
public class MethodEventMethodInterceptor implements MethodInterceptor, MethodInterceptorAccept {
	private final MethodEventDispatcher eventDispatcher;

	public MethodEventMethodInterceptor(MethodEventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		return invoker.getMethod().getAnnotation(PublishMethodEvent.class) != null;
	}

	public Object intercept(final MethodInvoker invoker, final Object[] args, MethodInterceptorChain filterChain) throws Throwable {
		final PublishMethodEvent publishEvent = invoker.getMethod().getAnnotation(PublishMethodEvent.class);
		if (publishEvent == null) {
			return filterChain.intercept(invoker, args);
		}

		MethodEvent event = new MethodEvent(filterChain.intercept(invoker, args), invoker, args);
		eventDispatcher.publishEvent(publishEvent.value(), event);
		return event.getResult();
	}

}
