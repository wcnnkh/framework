package scw.result;

public interface ResultFactory extends SuccessResultFactory, ErrorResultFactory {
	/**
	 * 授权失败
	 * 
	 * @return
	 */
	<T extends Result> T authorizationFailure();

	/**
	 * 参数错误
	 * 
	 * @return
	 */
	<T extends Result> T parameterError();
}
