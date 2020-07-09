package scw.event.method;

import scw.aop.Filter;
import scw.aop.ProxyInvoker;
import scw.core.instance.annotation.Configuration;
import scw.event.method.annotation.PublishMethodEvent;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

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

		final MethodEvent event = new MethodEvent(invoker, args);
		if (TransactionManager.hasTransaction()) {
			TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
				@Override
				public void afterCommit() {
					eventDispatcher.publishEvent(publishEvent.value(), event);
				}
			});
		} else {
			eventDispatcher.publishEvent(publishEvent.value(), event);
		}
		return event.getResult();
	}

}
