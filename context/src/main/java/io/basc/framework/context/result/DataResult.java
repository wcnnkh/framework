package io.basc.framework.context.result;

public class DataResult<T> extends Result {
	private static final long serialVersionUID = 1L;
	private T data;

	public DataResult() {
	}
	
	public DataResult(Result result) {
		super(result);
	}

	public DataResult(DataResult<T> result) {
		super(result);
		this.data = result.data;
	}

	public T getData() {
		return data;
	}

	@SuppressWarnings("unchecked")
	public DataResult<T> setData(Object data) {
		this.data = (T) data;
		return this;
	}

	@Override
	public Result setMsg(String msg) {
		super.setMsg(msg);
		return this;
	}

	@Override
	public DataResult<T> setRollbackOnlyResult(Boolean rollbackOnlyResult) {
		super.setRollbackOnlyResult(rollbackOnlyResult);
		return this;
	}

	public Result result() {
		return new Result(this);
	}
}
