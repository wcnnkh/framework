package scw.result;

import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Bean;
import scw.result.support.DefaultResultFactory;

@AutoImpl({ DefaultResultFactory.class })
@Bean("resultFactory")
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

	<T> DataResult<T> error(int code, String msg, T data, boolean rollback);

	<T> DataResult<T> error(String msg, T data, boolean rollback);
	
	<T> DataResult<T> error(int code, String msg);

	<T> DataResult<T> error(int code);

	<T> DataResult<T> error();

	<T> DataResult<T> error(String msg);

	<T> DataResult<T> error(Result result);

	<T> DataResult<T> success();

	<T> DataResult<T> success(T data);

	int getDefaultErrorCode();

	int getSuccessCode();

	int getAuthorizationFailureCode();

	int getParamterErrorCode();
}
