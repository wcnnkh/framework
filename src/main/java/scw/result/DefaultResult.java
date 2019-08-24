package scw.result;

import java.io.Serializable;

public class DefaultResult<T> extends DefaultBaseDataResult<T> implements DataResult<T>, Serializable{
	private static final long serialVersionUID = 1L;
	private int code;
	private boolean rollbackOnly;

	public DefaultResult(boolean success, int code, T data, String msg, boolean rollbackOnly) {
		super(success, msg, data);
		this.code = code;
		this.rollbackOnly = rollbackOnly;
	}

	public int getCode() {
		return code;
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

	public boolean isError() {
		return !isSuccess();
	}
}
