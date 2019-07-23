package scw.result;

import java.io.Serializable;

public class DefaultResult<T> implements DataResult<T>, Serializable{
	private static final long serialVersionUID = 1L;
	private boolean success = true;
	private int code;
	private T data;
	private String msg;
	private boolean rollbackOnly;

	protected DefaultResult() {
	}

	public DefaultResult(boolean success, int code, T data, String msg, boolean rollbackOnly) {
		this.success = success;
		this.code = code;
		this.data = data;
		this.msg = msg;
		this.rollbackOnly = rollbackOnly;
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
		return rollbackOnly;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isError() {
		return !success;
	}
}
