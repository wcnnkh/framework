package scw.event.method;

import scw.aop.Filter;
import scw.aop.ProxyInvoker;
import scw.core.instance.annotation.Configuration;
import scw.event.method.annotation.PublishMethodEvent;

@Configuration(order = Integer.MIN_VALUE)
public class MethodEventFilter implements Filter {
	private final MethodEventDispatcher eventDispatcher;

	public MethodEventFilter(MethodEventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	public Object doFilter(final ProxyInvoker invoker, final Object[] args) throws Throwable {
		final PublishMethodEvent publishEvent = invoker.getMethod().getAnnotation(PublishMethodEvent.class);
		if (publishEvent == null) {
			return invoker.invoke(args);
		}

		MethodEvent event = new MethodEvent(invoker, args);
		eventDispatcher.publishEvent(publishEvent.value(), event);
		return event.getResult();
	}

}
