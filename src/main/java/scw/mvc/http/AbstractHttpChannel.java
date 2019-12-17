package scw.mvc.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.core.parameter.ParameterConfig;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.json.JSONUtils;
import scw.json.JSONSupport;
import scw.mvc.MVCUtils;
import scw.mvc.parameter.AbstractParameterChannel;
import scw.net.http.Cookie;
import scw.security.session.Authorization;
import scw.security.session.Session;
import scw.security.session.http.HttpChannelAuthorization;
import scw.security.session.http.HttpChannelUserSessionFactory;
import scw.util.ip.IP;
import scw.util.ip.SimpleIP;

public abstract class AbstractHttpChannel extends AbstractParameterChannel implements HttpChannel {
	private static final String GET_DEFAULT_CHARSET_ANME = "ISO-8859-1";

	protected static final String JSONP_CALLBACK = "callback";
	protected static final String JSONP_RESP_PREFIX = "(";
	protected static final String JSONP_RESP_SUFFIX = ");";
	protected final boolean cookieValue;
	private final HttpParameterRequest request;
	private final HttpResponse response;
	private final String jsonp;

	public <R extends HttpRequest, P extends HttpResponse> AbstractHttpChannel(BeanFactory beanFactory,
			JSONSupport jsonParseSupport, boolean cookieValue,
			R request, P response, String jsonp) {
		super(beanFactory, jsonParseSupport);
		this.cookieValue = cookieValue;
		this.request = new HttpParameterRequest(request, this);
		this.response = response;
		this.jsonp = jsonp;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getParameter(ParameterConfig parameterConfig) {
		if (Session.class == parameterConfig.getType()) {
			return getRequest().getHttpSession();
		} else if (HttpParameterRequest.class == parameterConfig.getType()) {
			return getHttpParameterRequest();
		} else if (Authorization.class == parameterConfig.getType()) {
			HttpChannelUserSessionFactory httpChannelUserSessionFactory = getBean(HttpChannelUserSessionFactory.class);
			return new HttpChannelAuthorization(this, httpChannelUserSessionFactory);
		}else if(IP.class == parameterConfig.getType()){
			return new SimpleIP(request.getIP());
		}
		return super.getParameter(parameterConfig);
	}

	public Object getAttribute(String name) {
		return request.getAttribute(name);
	};

	public void setAttribute(String name, Object o) {
		request.setAttribute(name, o);
	}

	public Enumeration<String> getAttributeNames() {
		return request.getAttributeNames();
	}

	public void removeAttribute(String name) {
		request.removeAttribute(name);
	}

	public String decodeGETParameter(String value) {
		if (StringUtils.containsChinese(value)) {
			return value;
		}

		try {
			return new String(value.getBytes(GET_DEFAULT_CHARSET_ANME), request.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return value;
		}
	}

	public String getString(String name) {
		String v = request.getParameter(name);
		if (v == null) {
			Map<String, String> restParameterMap = MVCUtils.getRestPathParameterMap(this);
			if (restParameterMap != null) {
				v = restParameterMap.get(name);
			}
		}

		if (v == null) {
			if (cookieValue) {
				Cookie cookie = request.getCookie(name, false);
				if (cookie != null) {
					v = cookie.getValue();
				}
			}
		} else {
			if ("GET".equals(request.getMethod())) {
				v = decodeGETParameter(v);
			}
		}
		return v;
	}

	@SuppressWarnings("unchecked")
	public HttpRequest getRequest() {
		return request.getTargetHtpRequest();
	}

	@SuppressWarnings("unchecked")
	public HttpResponse getResponse() {
		return response;
	}

	public <E> E[] getArray(String name, Class<E> type) {
		String[] values = getRequest().getParameterValues(name);
		return StringParse.DEFAULT.getArray(values, type);
	}

	protected String getJsonpCallback() {
		String callbackTag = getString(jsonp);
		return StringUtils.isEmpty(callbackTag) ? null : callbackTag;
	}

	public void write(Object obj) throws Throwable {
		MVCUtils.httpWrite(this, jsonp, jsonParseSupport, obj);
	}

	public InputStream getInputStream() throws IOException {
		return getRequest().getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return getResponse().getOutputStream();
	}

	public HttpParameterRequest getHttpParameterRequest() {
		return request;
	}

	@Override
	public String toString() {
		StringBuilder appendable = new StringBuilder();
		appendable.append("path=").append(getRequest().getRequestPath());
		appendable.append(",method=").append(getRequest().getMethod());
		appendable.append(",").append(JSONUtils.toJSONString(getRequest().getParameterMap()));
		return appendable.toString();
	}
}
