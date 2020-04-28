package scw.util.result;

public class DataResult<T> extends Result {
	private static final long serialVersionUID = 1L;
	private T data;

	public DataResult() {
	};

	/**
	 * 不会设置data的值
	 * 
	 * @param result
	 */
	public DataResult(Result result) {
		setCode(result.getCode());
		setMsg(result.getMsg());
		setSuccess(result.isSuccess());
		setRollbackOnlyResult(result.getRollbackOnlyResult());
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
}
