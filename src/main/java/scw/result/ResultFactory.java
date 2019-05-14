package scw.result;

public interface ResultFactory extends SuccessResultFactory, ErrorResultFactory {
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
}
