package scw.result.support;

import scw.result.DataResult;
import scw.result.Result;
import scw.result.ResultFactory;

public abstract class AbstractResultFactory implements ResultFactory {
	private String contentType;
	private ResultMessageFactory resultMessageFactory;

	public AbstractResultFactory(ResultMessageFactory resultMessageFactory, String contentType) {
		this.resultMessageFactory = resultMessageFactory;
		this.contentType = contentType;
	}

	public String getMsg(int code) {
		return resultMessageFactory == null ? null : resultMessageFactory.getMessage(code);
	}

	public <T> DataResult<T> success() {
		return success(null);
	}

	public <T> DataResult<T> error(int code) {
		String msg = getMsg(code);
		return error(code, msg == null ? "操作失败" : msg);
	}

	public <T> DataResult<T> error(String msg) {
		return error(getDefaultErrorCode(), msg);
	}

	public <T> DataResult<T> error() {
		int code = getDefaultErrorCode();
		String msg = getMsg(code);
		return error(code, msg == null ? "系统错误" : msg);
	}

	public <T> DataResult<T> authorizationFailure() {
		int code = getAuthorizationFailureCode();
		String msg = getMsg(code);
		return error(code, msg == null ? "登录状态已过期" : msg);
	}

	public <T> DataResult<T> parameterError() {
		int code = getParamterErrorCode();
		String msg = getMsg(code);
		return error(code, msg == null ? "参数错误" : msg);
	}

	public <T> DataResult<T> error(Result result) {
		return error(result.getCode(), result.getMsg());
	}

	public <T> DataResult<T> success(T data) {
		int code = getSuccessCode();
		return new DefaultResult<T>(true, code, data, getMsg(code), contentType);
	}

	public <T> DataResult<T> error(int code, String msg) {
		return new DefaultResult<T>(false, code, null, msg, contentType);
	}
}
