package scw.result;

import scw.core.GlobalPropertyFactory;
import scw.event.support.DynamicValue;

public abstract class AbstractResultFactory implements ResultFactory {
	private static final DynamicValue<String> DEFAULT_ERROR_MESSAGE = GlobalPropertyFactory.getInstance()
			.getDynamicValue("result.error.msg", String.class, "系统错误");
	private static final DynamicValue<String> AUTHORIZATION_FAILURE_MESSAGE = GlobalPropertyFactory.getInstance()
			.getDynamicValue("result.authorization.failure.msg", String.class, "登录状态已过期");
	private static final DynamicValue<String> PARAMETER_ERROR_MESSAGE = GlobalPropertyFactory.getInstance()
			.getDynamicValue("result.parameter.error.msg", String.class, "参数错误");

	protected abstract String getMsg(long code);

	public <T> DataResult<T> success() {
		return success(null);
	}

	public <T> DataResult<T> error(long code) {
		String msg = getMsg(code);
		return error(code, msg == null ? DEFAULT_ERROR_MESSAGE.getValue() : msg);
	}

	public <T> DataResult<T> error(String msg) {
		return error(getDefaultErrorCode(), msg);
	}

	public <T> DataResult<T> error() {
		long code = getDefaultErrorCode();
		String msg = getMsg(code);
		return error(code, msg == null ? DEFAULT_ERROR_MESSAGE.getValue() : msg);
	}

	public <T> DataResult<T> authorizationFailure() {
		long code = getAuthorizationFailureCode();
		String msg = getMsg(code);
		return error(code, msg == null ? AUTHORIZATION_FAILURE_MESSAGE.getValue() : msg);
	}

	public <T> DataResult<T> parameterError() {
		long code = getParamterErrorCode();
		String msg = getMsg(code);
		return error(code, msg == null ? PARAMETER_ERROR_MESSAGE.getValue() : msg);
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
