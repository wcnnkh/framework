package io.basc.framework.context.transaction;

import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.value.observe.Observable;

public class ResultFactory {
	static final Observable<String> DEFAULT_ERROR_MESSAGE = Sys.getEnv().getProperties()
			.getObservable("result.error.msg").map((e) -> e.or("系统错误").getAsString());
	static final Observable<String> AUTHORIZATION_FAILURE_MESSAGE = Sys.getEnv().getProperties()
			.getObservable("result.authorization.failure.msg").map((e) -> e.or("授权失败").getAsString());
	static final Observable<String> PARAMETER_ERROR_MESSAGE = Sys.getEnv().getProperties()
			.getObservable("result.parameter.error.msg").map((e) -> e.or("参数错误").getAsString());

	private final ResultMessageFactory messageFactory;
	private final int defaultErrorCode;
	private final int successCode;
	private final int authorizationFailureCode;
	private final int parameterErrorCode;

	public ResultFactory() {
		this(null);
	}

	public ResultFactory(@Nullable ResultMessageFactory messageFactory) {
		this(messageFactory, 1, 0, -1, 2);
	}

	public ResultFactory(@Nullable ResultMessageFactory messageFactory, int defaultErrorCode, int successCode,
			int authorizationFailureCode, int parameterErrorCode) {
		this.messageFactory = messageFactory;
		this.defaultErrorCode = defaultErrorCode;
		this.successCode = successCode;
		this.authorizationFailureCode = authorizationFailureCode;
		this.parameterErrorCode = parameterErrorCode;
	}

	public final <T> DataResult<T> success() {
		return success(null);
	}

	public final <T> DataResult<T> error(long code) {
		String msg = getMessage(code);
		return error(code, msg == null ? DEFAULT_ERROR_MESSAGE.get() : msg);
	}

	public final <T> DataResult<T> error(String msg) {
		return error(getDefaultErrorCode(), msg);
	}

	public final <T> DataResult<T> error() {
		long code = getDefaultErrorCode();
		String msg = getMessage(code);
		return error(code, msg == null ? DEFAULT_ERROR_MESSAGE.get() : msg);
	}

	public final <T> DataResult<T> authorizationFailure() {
		long code = getAuthorizationFailureCode();
		String msg = getMessage(code);
		return error(code, msg == null ? AUTHORIZATION_FAILURE_MESSAGE.get() : msg);
	}

	public final <T> DataResult<T> parameterError() {
		long code = getParameterErrorCode();
		String msg = getMessage(code);
		return error(code, msg == null ? PARAMETER_ERROR_MESSAGE.get() : msg);
	}

	public final <T> DataResult<T> error(Result result) {
		return error(result.getCode(), result.getMsg());
	}

	public <T> DataResult<T> success(T data) {
		long code = getSuccessCode();
		return new DataResult<T>(true, code, getMessage(code), false, data);
	}

	public final <T> DataResult<T> error(long code, String msg) {
		if (code == getSuccessCode()) {
			throw new IllegalArgumentException("Error code cannot be " + getSuccessCode());
		}
		return error(code, msg, null);
	}

	public <T> DataResult<T> error(long code, String msg, T data) {
		return new DataResult<T>(false, code, msg, true, data);
	}

	public final <T> DataResult<T> error(String msg, T data) {
		return error(getDefaultErrorCode(), msg, data);
	}

	public final long getDefaultErrorCode() {
		return defaultErrorCode;
	}

	public final long getSuccessCode() {
		return successCode;
	}

	public final long getAuthorizationFailureCode() {
		return authorizationFailureCode;
	}

	public final long getParameterErrorCode() {
		return parameterErrorCode;
	}

	@Nullable
	public final ResultMessageFactory getMessageFactory() {
		return messageFactory;
	}

	protected String getMessage(long code) {
		return messageFactory == null ? null : messageFactory.getMessage(code);
	}
}
