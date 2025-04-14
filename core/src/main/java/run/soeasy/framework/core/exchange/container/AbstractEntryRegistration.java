package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.concurrent.limit.DisposableLimiter;

public abstract class AbstractEntryRegistration<K, V> extends LimitableRegistration implements EntryRegistration<K, V> {
	public AbstractEntryRegistration() {
		super(new DisposableLimiter());
	}
}
