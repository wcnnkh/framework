package scw.result.support;

import scw.beans.annotation.Bean;
import scw.result.AbstractResultFactory;
import scw.result.DataResult;

@Bean(proxy = false)
public class DefaultResultFactory extends AbstractResultFactory {
	private final int defaultErrorCode;
	private final int defaultSuccessCode;
	private final int authorizationFailureCode;
	private final int parameterErrorCode;

	public DefaultResultFactory(String propertiesFilePath, String charsetName, int defaultErrorCode,
			int defaultSuccessCode, int authorizationFailureCode, int parameterErrorCode) {
		super(propertiesFilePath, charsetName);
		this.defaultErrorCode = defaultErrorCode;
		this.defaultSuccessCode = defaultSuccessCode;
		this.authorizationFailureCode = authorizationFailureCode;
		this.parameterErrorCode = parameterErrorCode;
	}

	public <T> DataResult<T> success(int code, T data, String msg) {
		return new DefaultResult<T>(true, code, data, msg);
	}

	public <T> DataResult<T> error(int code, String msg) {
		return new DefaultResult<T>(false, code, null, msg);
	}

	@Override
	public int getDefaultErrorCode() {
		return defaultErrorCode;
	}

	@Override
	public int getDefaultSuccessCode() {
		return defaultSuccessCode;
	}

	@Override
	public int getAuthorizationFailureCode() {
		return authorizationFailureCode;
	}

	@Override
	public int getParamterErrorCode() {
		return parameterErrorCode;
	}

}
