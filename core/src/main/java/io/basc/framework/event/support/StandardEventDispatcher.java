package io.basc.framework.event.support;

import java.util.concurrent.Executor;

import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Select;
import io.basc.framework.util.Selector;
import io.basc.framework.util.SharedResultSet;

public class StandardEventDispatcher<T> implements EventDispatcher<T> {
	private final SharedResultSet<EventListener<T>> listeners = new SharedResultSet<>();
	private final Select<EventListener<T>> select;
	private final Executor executor;

	/**
	 * @param selector 如果为空说明不存在选择器，那么将消息推给所有的监听
	 * @param executor 异步执行器
	 */
	public StandardEventDispatcher(@Nullable Selector<EventListener<T>> selector, @Nullable Executor executor) {
		this.select = new Select<>(listeners, selector);
		this.executor = executor;
	}

	@Override
	public Registration registerListener(EventListener<T> eventListener) {
		Assert.requiredArgument(eventListener != null, "eventListener");
		return listeners.register(eventListener);
	}

	@Override
	public void publishEvent(T event) {
		if (executor == null) {
			syncPublishEvent(event);
		} else {
			executor.execute(() -> syncPublishEvent(event));
		}
	}

	public void syncPublishEvent(T event) {
		select.consume((e) -> e.onEvent(event));
	}

	public SharedResultSet<EventListener<T>> getListeners() {
		return listeners;
	}

	public Select<EventListener<T>> getSelect() {
		return select;
	}

	public void unregisterListeners() {
		listeners.clear();
	}
}
