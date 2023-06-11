package io.basc.framework.tx;

public class MergeSynchronization implements Synchronization {
	private final Synchronization left;
	private final Synchronization right;

	public MergeSynchronization(Synchronization left, Synchronization right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public void beforeCompletion() throws Throwable {
		left.beforeCompletion();
		right.beforeCompletion();
	}

	@Override
	public void afterCompletion(Status status) {
		if (status.isCompleted()) {
			try {
				right.afterCompletion(status);
			} finally {
				left.afterCompletion(status);
			}
		} else {
			try {
				left.afterCompletion(status);
			} finally {
				right.afterCompletion(status);
			}
		}
	}

}
