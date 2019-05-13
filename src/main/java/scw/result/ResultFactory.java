package scw.result;

public interface ResultFactory extends SuccessResultFactory, ErrorResultFactory {
	/**
	 * 授权失败
	 * @return
	 */
	<T extends Result> T authorizationFailure();
}
