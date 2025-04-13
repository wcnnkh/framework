package run.soeasy.framework.core.register.container;

import run.soeasy.framework.core.concurrent.limit.DisposableLimiter;
import run.soeasy.framework.core.register.LimitableRegistration;

public abstract class AbstractEntryRegistration<K, V> extends LimitableRegistration implements EntryRegistration<K, V> {
	public AbstractEntryRegistration() {
		super(new DisposableLimiter());
	}
}
