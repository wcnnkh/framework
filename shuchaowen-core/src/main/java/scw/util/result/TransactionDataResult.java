package scw.util.result;

import scw.transaction.RollbackOnlyResult;

public class TransactionDataResult<T> extends DataResult<T> implements
		RollbackOnlyResult {
	private static final long serialVersionUID = 1L;
	private boolean rollbackOnly;

	public TransactionDataResult(boolean success, String code, String msg, T data,
			boolean rollbackOnly) {
		super(success, code, msg, data);
		this.rollbackOnly = rollbackOnly;
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

}
