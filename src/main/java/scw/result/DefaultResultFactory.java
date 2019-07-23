package scw.result;

import scw.beans.annotation.Bean;

@Bean(proxy = false)
public class DefaultResultFactory extends AbstractResultFactory {
	private final int defaultErrorCode;
	private final int successCode;
	private final int authorizationFailureCode;
	private final int parameterErrorCode;

	public DefaultResultFactory(String propertiesFilePath, String charsetName, int defaultErrorCode, int successCode,
			int authorizationFailureCode, int parameterErrorCode) {
		this(propertiesFilePath, charsetName, defaultErrorCode, successCode, authorizationFailureCode,
				parameterErrorCode, true);
	}

	public DefaultResultFactory(String propertiesFilePath, String charsetName, int defaultErrorCode, int successCode,
			int authorizationFailureCode, int parameterErrorCode, boolean defaultRollbackOnly) {
		super(defaultRollbackOnly, propertiesFilePath, charsetName);
		this.defaultErrorCode = defaultErrorCode;
		this.successCode = successCode;
		this.authorizationFailureCode = authorizationFailureCode;
		this.parameterErrorCode = parameterErrorCode;
	}

	public <T> DataResult<T> error(int code, String msg, T data, boolean rollbackOnly) {
		return new DefaultResult<T>(false, code, data, msg, rollbackOnly);
	}

	public int getDefaultErrorCode() {
		return defaultErrorCode;
	}

	public int getSuccessCode() {
		return successCode;
	}

	public int getAuthorizationFailureCode() {
		return authorizationFailureCode;
	}

	public int getParamterErrorCode() {
		return parameterErrorCode;
	}

	public <T> DataResult<T> success(T data) {
		int code = getSuccessCode();
		return new DefaultResult<T>(true, code, data, getMsg(code), false);
	}

}
