package scw.event.method;

import scw.aop.Filter;
import scw.aop.FilterAccept;
import scw.aop.FilterChain;
import scw.aop.MethodInvoker;
import scw.core.instance.annotation.Configuration;
import scw.event.method.annotation.PublishMethodEvent;

@Configuration(order = Integer.MIN_VALUE)
public class MethodEventFilter implements Filter, FilterAccept {
	private final MethodEventDispatcher eventDispatcher;

	public MethodEventFilter(MethodEventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	public boolean isAccept(MethodInvoker invoker, Object[] args) {
		return invoker.getMethod().getAnnotation(PublishMethodEvent.class) != null;
	}

	public Object doFilter(final MethodInvoker invoker, final Object[] args, FilterChain filterChain) throws Throwable {
		final PublishMethodEvent publishEvent = invoker.getMethod().getAnnotation(PublishMethodEvent.class);
		if (publishEvent == null) {
			return filterChain.doFilter(invoker, args);
		}

		MethodEvent event = new MethodEvent(filterChain.doFilter(invoker, args), invoker, args);
		eventDispatcher.publishEvent(publishEvent.value(), event);
		return event.getResult();
	}

}
