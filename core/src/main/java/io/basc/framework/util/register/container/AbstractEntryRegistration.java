package io.basc.framework.util.register.container;

import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import io.basc.framework.util.register.LimitableRegistration;

public abstract class AbstractEntryRegistration<K, V> extends LimitableRegistration implements EntryRegistration<K, V> {
	public AbstractEntryRegistration() {
		super(new DisposableLimiter());
	}
}
