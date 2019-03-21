package scw.transaction;

final class AnnotationTransactionDefinition implements TransactionDefinition {
	private Transactional clzTx;
	private Transactional methodTx;

	public AnnotationTransactionDefinition(Transactional clzTx, Transactional methodTx) {
		this.clzTx = clzTx;
		this.methodTx = methodTx;
	}

	public Propagation getPropagation() {
		if (clzTx == null) {
			return methodTx == null ? Propagation.REQUIRED : methodTx.propagation();
		} else {
			return methodTx == null ? clzTx.propagation() : methodTx.propagation();
		}
	}

	public Isolation getIsolation() {
		if (clzTx == null) {
			return methodTx == null ? Isolation.DEFAULT : methodTx.isolation();
		} else {
			return methodTx == null ? clzTx.isolation() : methodTx.isolation();
		}
	}

	public int getTimeout() {
		if (clzTx == null) {
			return methodTx == null ? -1 : methodTx.timeout();
		} else {
			return methodTx == null ? clzTx.timeout() : methodTx.timeout();
		}
	}

	public boolean isReadOnly() {
		if (clzTx == null) {
			return methodTx == null ? false : methodTx.readOnly();
		} else {
			return methodTx == null ? clzTx.readOnly() : methodTx.readOnly();
		}
	}

}
