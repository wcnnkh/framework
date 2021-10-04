package io.basc.framework.context.result;

import io.basc.framework.context.transaction.RollbackOnlyResult;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.Status;

import java.io.Serializable;

public class Result implements Status<Long>, ResultMsgCode, RollbackOnlyResult, Serializable {
	private static final long serialVersionUID = 1L;
	private long code = 0;
	private Boolean rollbackOnlyResult;
	private String msg;
	private boolean success = true;

	public Result() {
	}

	public Result(Result result) {
		this.success = result.success;
		this.msg = result.msg;
		this.code = result.code;
		this.rollbackOnlyResult = result.rollbackOnlyResult;
	}

	public String getMsg() {
		return msg;
	}

	public final long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isError() {
		return !isSuccess();
	}

	public boolean isRollbackOnly() {
		return rollbackOnlyResult == null ? isError() : rollbackOnlyResult;
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

	public <T> DataResult<T> dataResult() {
		return dataResult(null);
	}

	public <T> DataResult<T> dataResult(T data) {
		DataResult<T> dataResult = new DataResult<T>(this);
		if (data != null) {
			dataResult.setData(data);
		}
		return dataResult;
	}

	@Override
	public final Long get() {
		return code;
	}

	@Override
	public final boolean isActive() {
		return isSuccess();
	}

	@Override
	public String toString() {
		return MapperUtils.toString(this);
	}

	public static Result success(long code) {
		Result result = new Result();
		result.setCode(code);
		result.setSuccess(true);
		return result;
	}

	public static Result error(long code, String message) {
		Result result = new Result();
		result.setCode(code);
		result.setMsg(message);
		result.setSuccess(false);
		return result;
	}
}
