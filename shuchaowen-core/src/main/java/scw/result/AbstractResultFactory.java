package scw.result;

public abstract class AbstractResultFactory implements ResultFactory {
	protected abstract String getMsg(long code);

	public <T> DataResult<T> success() {
		return success(null);
	}

	public <T> DataResult<T> error(long code) {
		String msg = getMsg(code);
		return error(code, msg == null ? "操作失败" : msg);
	}

	public <T> DataResult<T> error(String msg) {
		return error(getDefaultErrorCode(), msg);
	}

	public <T> DataResult<T> error() {
		long code = getDefaultErrorCode();
		String msg = getMsg(code);
		return error(code, msg == null ? "系统错误" : msg);
	}

	public <T> DataResult<T> authorizationFailure() {
		long code = getAuthorizationFailureCode();
		String msg = getMsg(code);
		return error(code, msg == null ? "登录状态已过期" : msg);
	}

	public <T> DataResult<T> parameterError() {
		long code = getParamterErrorCode();
		String msg = getMsg(code);
		return error(code, msg == null ? "参数错误" : msg);
	}

	public <T> DataResult<T> error(Result result) {
		return error(result.getCode(), result.getMsg());
	}

	public <T> DataResult<T> success(T data) {
		long code = getSuccessCode();
		DataResult<T> dataResult = new DataResult<T>();
		dataResult.setSuccess(true);
		dataResult.setCode(code);
		dataResult.setData(data);
		dataResult.setMsg(getMsg(code));
		return dataResult;
	}

	public <T> DataResult<T> error(long code, String msg) {
		return error(code, msg, null);
	}

	public <T> DataResult<T> error(long code, String msg, Object data) {
		DataResult<T> dataResult = new DataResult<T>();
		dataResult.setCode(code);
		dataResult.setSuccess(false);
		dataResult.setMsg(msg);
		dataResult.setData(data);
		return dataResult;
	}

	public <T> DataResult<T> error(String msg, Object data) {
		return error(getDefaultErrorCode(), msg, data);
	}
}
