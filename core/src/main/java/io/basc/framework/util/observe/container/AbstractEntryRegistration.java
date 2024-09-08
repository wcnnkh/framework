package io.basc.framework.util.observe.container;

import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import io.basc.framework.util.observe.LimitableRegistration;

public abstract class AbstractEntryRegistration<K, V> extends LimitableRegistration implements EntryRegistration<K, V> {

	public AbstractEntryRegistration() {
		super(new DisposableLimiter());
	}
}
