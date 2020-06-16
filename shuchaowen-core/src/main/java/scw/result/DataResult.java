package scw.result;

public class DataResult<T> extends Result {
	private static final long serialVersionUID = 1L;
	private T data;

	public DataResult() {
	};

	public T getData() {
		return data;
	}

	@SuppressWarnings("unchecked")
	public DataResult<T> setData(Object data) {
		this.data = (T) data;
		return this;
	}

	@Override
	public DataResult<T> setCode(long code) {
		super.setCode(code);
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

	@Override
	public DataResult<T> setSuccess(boolean success) {
		super.setSuccess(success);
		return this;
	}
	
	public Result result(){
		Result result = new Result(getCode(), getMsg());
		result.setRollbackOnlyResult(result.getRollbackOnlyResult());
		result.setSuccess(isSuccess());
		return result;
	}
}
