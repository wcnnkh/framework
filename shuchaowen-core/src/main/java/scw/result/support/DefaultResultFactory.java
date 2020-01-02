package scw.result.support;

import scw.beans.annotation.Bean;
import scw.core.annotation.DefaultValue;
import scw.core.annotation.ParameterName;
import scw.lang.Nullable;
import scw.net.mime.MimeTypeConstants;
import scw.result.ResultMessageFactory;

@Bean(proxy = false)
public class DefaultResultFactory extends AbstractResultFactory {
	private final int defaultErrorCode;
	private final int successCode;
	private final int authorizationFailureCode;
	private final int parameterErrorCode;

	public DefaultResultFactory(@Nullable ResultMessageFactory resultMessageFactory,
			@ParameterName("result.error.code") @DefaultValue("1") int defaultErrorCode,
			@ParameterName("result.success.code") @DefaultValue("0") int successCode,
			@ParameterName("result.authoriaztion.fail.code") @DefaultValue("-1") int authorizationFailureCode,
			@ParameterName("result.parameter.error.code") @DefaultValue("2") int parameterErrorCode,
			@ParameterName("result.contentType") @DefaultValue(MimeTypeConstants.APPLICATION_JSON_VALUE) String contentType) {
		super(resultMessageFactory, contentType);
		this.defaultErrorCode = defaultErrorCode;
		this.successCode = successCode;
		this.authorizationFailureCode = authorizationFailureCode;
		this.parameterErrorCode = parameterErrorCode;
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
}
