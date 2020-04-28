package scw.util.result;

import java.io.Serializable;

import scw.transaction.RollbackOnlyResult;

public class Result implements Serializable, RollbackOnlyResult {
	private static final long serialVersionUID = 1L;
	private boolean success;
	private long code;
	private String msg;
	private Boolean rollbackOnlyResult;

	public Result() {
	};

	public Result(long code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public boolean isSuccess() {
		return success;
	}

	public long getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public boolean isError() {
		return !isSuccess();
	}

	public boolean isRollbackOnly() {
		return rollbackOnlyResult == null ? isError() : rollbackOnlyResult;
	}

	public Result setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public Result setCode(long code) {
		this.code = code;
		return this;
	}

	public Result setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public Boolean getRollbackOnlyResult() {
		return rollbackOnlyResult;
	}

	/**
	 * 一般情况下不用设置此值
	 * 
	 * @param rollbackOnlyResult
	 */
	public Result setRollbackOnlyResult(Boolean rollbackOnlyResult) {
		this.rollbackOnlyResult = rollbackOnlyResult;
		return this;
	}
}
