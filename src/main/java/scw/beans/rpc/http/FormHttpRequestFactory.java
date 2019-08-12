package scw.beans.rpc.http;

import java.io.OutputStream;
import java.util.Map;

import scw.core.Constants;
import scw.net.ContentType;
import scw.net.DefaultContentType;
import scw.net.http.HttpRequest;
import scw.net.http.HttpUtils;

public final class FormHttpRequestFactory extends AbstractRPCRequestFactory {
	public FormHttpRequestFactory() {
		this(Constants.DEFAULT_CHARSET_NAME);
	}

	public FormHttpRequestFactory(String charsetName) {
		super(charsetName);
	}

	@Override
	protected void writeParameters(Map<String, Object> parameterMap, OutputStream output) throws Exception {
		output.write(HttpUtils.appendParameters(null, parameterMap, getCharsetName()).getBytes(getCharsetName()));
	}

	@Override
	protected void afterHttpRequest(HttpRequest httpRequest) throws Exception {
		httpRequest.setContentType(
				new DefaultContentType(ContentType.APPLICATION_X_WWW_FORM_URLENCODED, getCharsetName()));
	}

}
