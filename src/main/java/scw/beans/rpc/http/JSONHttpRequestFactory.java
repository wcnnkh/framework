package scw.beans.rpc.http;

import java.io.OutputStream;
import java.util.Map;

import scw.core.Constants;
import scw.json.JSONUtils;
import scw.net.ContentType;
import scw.net.DefaultContentType;
import scw.net.http.HttpRequest;

public class JSONHttpRequestFactory extends AbstractRPCRequestFactory {
	public JSONHttpRequestFactory() {
		this(Constants.DEFAULT_CHARSET_NAME);
	}

	public JSONHttpRequestFactory(String charsetName) {
		super(charsetName);
	}

	@Override
	protected void writeParameters(Map<String, Object> parameterMap, OutputStream output) throws Exception {
		output.write(JSONUtils.toJSONString(parameterMap).getBytes(getCharsetName()));
	}

	@Override
	protected void afterHttpRequest(HttpRequest httpRequest) throws Exception {
		httpRequest.setContentType(new DefaultContentType(ContentType.APPLICATION_JSON, getCharsetName()));
	}

}
