package scw.result;

import scw.beans.annotation.AopEnable;
import scw.beans.annotation.AutoImpl;

@AutoImpl({ DefaultResultFactory.class })
@AopEnable(false)
public interface ResultFactory {

	/**
	 * 授权失败
	 * 
	 * @return
	 */
	<T> DataResult<T> authorizationFailure();

	/**
	 * 参数错误
	 * 
	 * @return
	 */
	<T> DataResult<T> parameterError();

	<T> DataResult<T> error(long code, String msg, Object data);

	<T> DataResult<T> error(String msg, Object data);

	<T> DataResult<T> error(long code, String msg);

	<T> DataResult<T> error(long code);

	<T> DataResult<T> error();

	<T> DataResult<T> error(String msg);

	<T> DataResult<T> success();

	<T> DataResult<T> success(T data);

	long getDefaultErrorCode();

	long getSuccessCode();

	long getAuthorizationFailureCode();

	long getParamterErrorCode();
}
