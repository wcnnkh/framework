package scw.context.result;

import scw.context.transaction.RollbackOnlyResult;

public class Result extends BaseResult implements RollbackOnlyResult, ErrorCode {
	private static final long serialVersionUID = 1L;
	private final long code;
	private Boolean rollbackOnlyResult;

	public Result(boolean success, long code) {
		super(success);
		this.code = code;
	}

	public Result(Result result) {
		super(result);
		this.code = result.code;
		this.rollbackOnlyResult = result.rollbackOnlyResult;
	}

	public final long getCode() {
		return code;
	}

	public boolean isError() {
		return !isSuccess();
	}

	public boolean isRollbackOnly() {
		return rollbackOnlyResult == null ? isError() : rollbackOnlyResult;
	}

	public Result setMsg(String msg) {
		super.setMsg(msg);
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
}
