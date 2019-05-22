package scw.result.support.servlet;

import scw.result.DataResult;
import scw.result.support.DefaultResultFactory;

public final class ServletResultFactory extends DefaultResultFactory {
	private final String contentType;
	private final int timeout;

	public ServletResultFactory(String propertiesFilePath, String charsetName,
			int defaultErrorCode, int defaultSuccessCode, int loginExpiredCode,
			int parameterErrorCode, String contentType) {
		this(propertiesFilePath, charsetName, defaultErrorCode,
				defaultSuccessCode, loginExpiredCode, parameterErrorCode,
				contentType, 100);
	}

	public ServletResultFactory(String propertiesFilePath, String charsetName,
			int defaultErrorCode, int defaultSuccessCode, int loginExpiredCode,
			int parameterErrorCode, String contentType, int timeout) {
		super(propertiesFilePath, charsetName, defaultErrorCode,
				defaultSuccessCode, loginExpiredCode, parameterErrorCode);
		this.contentType = contentType;
		this.timeout = timeout;
	}

	public <T> DataResult<T> success(int code, T data, String msg) {
		return new ServletViewResult<T>(true, code, data, msg, contentType,
				timeout);
	}

	public <T> DataResult<T> error(int code, String msg) {
		return new ServletViewResult<T>(false, code, null, msg, contentType,
				timeout);
	}
}
