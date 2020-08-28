package scw.result;

import scw.core.parameter.annotation.DefaultValue;
import scw.core.parameter.annotation.ParameterName;
import scw.lang.Nullable;

public class DefaultResultFactory extends AbstractResultFactory {
	private ResultMessageFactory resultMessageFactory;
	private final int defaultErrorCode;
	private final int successCode;
	private final int authorizationFailureCode;
	private final int parameterErrorCode;

	public DefaultResultFactory(@Nullable ResultMessageFactory resultMessageFactory,
			@ParameterName("result.error.code") @DefaultValue("1") int defaultErrorCode,
			@ParameterName("result.success.code") @DefaultValue("0") int successCode,
			@ParameterName("result.authoriaztion.fail.code") @DefaultValue("-1") int authorizationFailureCode,
			@ParameterName("result.parameter.error.code") @DefaultValue("2") int parameterErrorCode) {
		this.resultMessageFactory = resultMessageFactory;
		this.defaultErrorCode = defaultErrorCode;
		this.successCode = successCode;
		this.authorizationFailureCode = authorizationFailureCode;
		this.parameterErrorCode = parameterErrorCode;
	}

	public long getDefaultErrorCode() {
		return defaultErrorCode;
	}

	public long getSuccessCode() {
		return successCode;
	}

	public long getAuthorizationFailureCode() {
		return authorizationFailureCode;
	}

	public long getParamterErrorCode() {
		return parameterErrorCode;
	}

	@Override
	protected String getMsg(long code) {
		return resultMessageFactory == null ? null : resultMessageFactory.getMessage(code);
	}
}
