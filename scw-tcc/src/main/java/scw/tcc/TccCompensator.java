package scw.tcc;

import scw.consistency.Compensator;
import scw.transaction.TransactionLifecycle;

public class TccCompensator implements TransactionLifecycle {
	private final Compensator confirm;
	private final Compensator cancel;

	public TccCompensator(Compensator confirm, Compensator cancel) {
		this.confirm = confirm;
		this.cancel = cancel;
	}

	@Override
	public void complete() {
	}

	@Override
	public void beforeCommit() throws Throwable {
	}

	@Override
	public void afterCommit() {
		if (cancel != null) {
			cancel.cancel();
		}

		if (confirm != null) {
			confirm.run();
		}
	}

	@Override
	public void beforeRollback() {
	}

	@Override
	public void afterRollback() {
		if (confirm != null) {
			confirm.cancel();
		}

		if (cancel != null) {
			cancel.run();
		}
	}

}
