package scw.util.result;

public interface ResultFactory {
	<T> DataResult<T> success();
	
	<T> DataResult<T> error();
	
	<T> DataResult<T> success(T data);
	
	<T> DataResult<T> error(String msg);
	
	<T> DataResult<T> error(String code, String msg);
	
	String getSuccessCode();
	
	String getErrorCode();
	
	/**
	 * 权限不足
	 * @return
	 */
	<T> DataResult<T> insufficientAuthority();
	
	/**
	 * 登录状态已过期
	 * @return
	 */
	<T> DataResult<T> loginStatusExpired();
	
	String getInsufficientAuthorityCode();
	
	String getLoginStatusExpiredCode();
	
	<T> DataResult<T> success(String code, String msg, T data);
	
	<T> DataResult<T> error(String code, String msg, T data);
}
