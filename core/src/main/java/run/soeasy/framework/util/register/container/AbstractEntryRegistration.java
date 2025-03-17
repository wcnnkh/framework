package run.soeasy.framework.util.register.container;

import run.soeasy.framework.util.concurrent.limit.DisposableLimiter;
import run.soeasy.framework.util.register.LimitableRegistration;

public abstract class AbstractEntryRegistration<K, V> extends LimitableRegistration implements EntryRegistration<K, V> {
	public AbstractEntryRegistration() {
		super(new DisposableLimiter());
	}
}
