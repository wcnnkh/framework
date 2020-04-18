package scw.util.result;

import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultResultFactory implements ResultFactory {

	public <T> DataResult<T> success() {
		return success(null);
	}

	public <T> DataResult<T> error() {
		return error("error");
	}

	public String getSuccessCode() {
		return "0";
	}

	public String getErrorCode() {
		return "1";
	}

	public <T> DataResult<T> insufficientAuthority() {
		return error(getInsufficientAuthorityCode(), "权限不足");
	}

	public <T> DataResult<T> loginStatusExpired() {
		return error(getLoginStatusExpiredCode(), "登录状态已过期或已在其他地方登录");
	}

	public String getInsufficientAuthorityCode() {
		return "3";
	}

	public String getLoginStatusExpiredCode() {
		return "2";
	}

	public <T> DataResult<T> success(String code, String msg, T data) {
		return new TransactionDataResult<T>(true, code, msg, data, false);
	}

	public <T> DataResult<T> error(String code, String msg, T data) {
		return new TransactionDataResult<T>(false, code, msg, data, true);
	}

	public <T> DataResult<T> success(T data) {
		return success(getSuccessCode(), "success", data);
	}

	public <T> DataResult<T> error(String msg) {
		return error(getErrorCode(), msg);
	}

	public <T> DataResult<T> error(String code, String msg) {
		return error(code, msg, null);
	}

}
