package scw.result.support;

import scw.beans.annotation.Bean;
import scw.core.Constants;
import scw.result.AbstractResultFactory;
import scw.result.DataResult;
import scw.result.Result;

@Bean(proxy=false)
public class DefaultResultFactory extends AbstractResultFactory {
	private final int defaultErrorCode;
	private final int defaultSuccessCode;
	private final int authorizationFailureCode;

	public DefaultResultFactory(String propertiesFilePath) {
		this(propertiesFilePath, Constants.DEFAULT_CHARSET.name(), 1, 0, -1);
	}

	public DefaultResultFactory(String propertiesFilePath, String charsetName, int defaultErrorCode,
			int defaultSuccessCode, int authorizationFailureCode) {
		super(propertiesFilePath, charsetName);
		this.defaultErrorCode = defaultErrorCode;
		this.defaultSuccessCode = defaultSuccessCode;
		this.authorizationFailureCode = authorizationFailureCode;
	}

	@SuppressWarnings("unchecked")
	public <D, T extends DataResult<D>> T success(int code, D data, String msg) {
		return (T) new DefaultResult<D>(true, code, data, msg);
	}

	@SuppressWarnings("unchecked")
	public <T extends Result> T error(int code, String msg) {
		return (T) new DefaultResult<T>(false, code, null, msg);
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

}
