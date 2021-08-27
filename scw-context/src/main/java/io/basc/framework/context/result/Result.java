package io.basc.framework.context.result;

import io.basc.framework.context.transaction.RollbackOnlyResult;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.Status;

import java.io.Serializable;

public class Result implements Status<Long>, ResultMsgCode, RollbackOnlyResult, Serializable {
	private static final long serialVersionUID = 1L;
	private final long code;
	private Boolean rollbackOnlyResult;
	private String msg;
	private boolean success;

	public Result(boolean success, long code) {
		this.success = success;
		this.code = code;
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
		DataResult<T> dataResult = new DataResult<T>(isSuccess(), code);
		dataResult.setMsg(getMsg()).setRollbackOnlyResult(getRollbackOnlyResult());
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
}
