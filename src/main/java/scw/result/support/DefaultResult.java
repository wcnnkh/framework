package scw.result.support;

import java.io.Serializable;

import scw.result.DataResult;
import scw.transaction.RollbackOnlyResult;

public class DefaultResult<T> implements DataResult<T>, Serializable, RollbackOnlyResult {
	private static final long serialVersionUID = 1L;
	private boolean success = true;
	private int code;
	private T data;
	private String msg;

	protected DefaultResult() {
	}

	public DefaultResult(boolean success, int code, T data, String msg) {
		this.success = success;
		this.code = code;
		this.data = data;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public boolean isSuccess() {
		return success;
	}

	public T getData() {
		return data;
	}

	public boolean isRollbackOnly() {
		return !isSuccess();
	}

}
