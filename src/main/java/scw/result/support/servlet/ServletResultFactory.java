package scw.result.support.servlet;

import scw.beans.annotation.Bean;
import scw.result.DataResult;
import scw.result.Result;
import scw.result.support.DefaultResultFactory;

@Bean(proxy = false)
public final class ServletResultFactory extends DefaultResultFactory {
	private final String contentType;

	public ServletResultFactory(String propertiesFilePath, String contentType) {
		super(propertiesFilePath);
		this.contentType = contentType;
	}

	public ServletResultFactory(String propertiesFilePath, String charsetName,
			int defaultErrorCode, int defaultSuccessCode, int loginExpiredCode,
			int parameterErrorCode, String contentType) {
		super(propertiesFilePath, charsetName, defaultErrorCode,
				defaultSuccessCode, loginExpiredCode, parameterErrorCode);
		this.contentType = contentType;
	}

	@SuppressWarnings("unchecked")
	public <D, T extends DataResult<? super D>> T success(int code, D data,
			String msg) {
		return (T) new ServletViewResult<D>(true, code, data, msg, contentType);
	}

	@SuppressWarnings("unchecked")
	public <T extends Result> T error(int code, String msg) {
		return (T) new ServletViewResult<T>(false, code, null, msg, contentType);
	}
}
