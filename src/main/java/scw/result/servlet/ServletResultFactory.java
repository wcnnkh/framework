package scw.result.servlet;

import scw.result.DataResult;
import scw.result.DefaultResultFactory;

public final class ServletResultFactory extends DefaultResultFactory {
	private final String contentType;

	public ServletResultFactory(String propertiesFilePath, String charsetName, int defaultErrorCode,
			int defaultSuccessCode, int loginExpiredCode, int parameterErrorCode, String contentType) {
		this(propertiesFilePath, charsetName, defaultErrorCode, defaultSuccessCode, loginExpiredCode,
				parameterErrorCode, contentType, true);
	}

	public ServletResultFactory(String propertiesFilePath, String charsetName, int defaultErrorCode,
			int defaultSuccessCode, int loginExpiredCode, int parameterErrorCode, String contentType,
			boolean defaultRollbackOnly) {
		super(propertiesFilePath, charsetName, defaultErrorCode, defaultSuccessCode, loginExpiredCode,
				parameterErrorCode, defaultRollbackOnly);
		this.contentType = contentType;
	}

	@Override
	public <T> DataResult<T> success(T data) {
		int code = getSuccessCode();
		return new ServletTextResult<T>(true, code, data, getMsg(code), contentType, false);
	}

	@Override
	public <T> DataResult<T> error(int code, String msg, T data, boolean rollbackOnly) {
		return new ServletTextResult<T>(false, code, data, msg, contentType, rollbackOnly);
	}
}
