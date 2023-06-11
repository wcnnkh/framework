package io.basc.framework.tcc;

import io.basc.framework.consistency.Compensator;
import io.basc.framework.tx.Status;
import io.basc.framework.tx.Synchronization;

public class TccSynchronization implements Synchronization {
	private final Compensator confirm;
	private final Compensator cancel;

	public TccSynchronization(Compensator confirm, Compensator cancel) {
		this.confirm = confirm;
		this.cancel = cancel;
	}

	@Override
	public void beforeCompletion() {
		if (cancel != null) {
			cancel.cancel();
		}

		if (confirm != null) {
			confirm.run();
		}
	}

	@Override
	public void afterCompletion(Status status) {
		if (status.equals(Status.ROLLED_BACK)) {
			if (confirm != null) {
				confirm.cancel();
			}

			if (cancel != null) {
				cancel.run();
			}
		}
	}
}
