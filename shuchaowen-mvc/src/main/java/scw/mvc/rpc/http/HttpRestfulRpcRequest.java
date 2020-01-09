package scw.mvc.rpc.http;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.json.JSONUtils;
import scw.net.header.HeadersConstants;
import scw.net.http.HttpRequest;
import scw.net.http.HttpUtils;
import scw.net.http.Method;
import scw.util.MimeTypeUtils;

public class HttpRestfulRpcRequest extends HttpRequest {
	private Map<String, Object> parameterMap = new HashMap<String, Object>();
	private String charsetName;

	public HttpRestfulRpcRequest(Method method, String requestUrl, String charsetName)
			throws UnsupportedEncodingException {
		super(method, requestUrl);
		this.charsetName = charsetName;
		String ip = MvcRpcUtils.getIP();
		if (StringUtils.isNotEmpty(ip)) {
			setRequestProperties(HeadersConstants.X_FORWARDED_FOR, ip);
		}
	}

	@Override
	public String getRequestUrl() {
		if (getMethod() == Method.GET) {
			try {
				return HttpUtils.appendParameters(super.getRequestUrl(), parameterMap, charsetName);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return super.getRequestUrl();
	}

	public Map<String, Object> getParameterMap() {
		return Collections.unmodifiableMap(parameterMap);
	}

	public void putAll(Map<String, Object> parameterMap) {
		this.parameterMap.putAll(parameterMap);
	}

	public void put(String name, Object value) {
		parameterMap.put(name, value);
	}

	@Override
	protected void doOutput(URLConnection urlConnection, OutputStream os) throws Throwable {
		if (getMethod() == Method.GET && !CollectionUtils.isEmpty(parameterMap)) {
			String body = isJsonRequest(urlConnection) ? JSONUtils.toJSONString(parameterMap)
					: HttpUtils.appendParameters(null, parameterMap, charsetName);
			os.write(body.getBytes(charsetName));
		}
		super.doOutput(urlConnection, os);
	}

	protected boolean isJsonRequest(URLConnection urlConnection) {
		return StringUtils.contains(urlConnection.getRequestProperty("Content-Type"),
				MimeTypeUtils.APPLICATION_JSON_VALUE, true);
	}
}
